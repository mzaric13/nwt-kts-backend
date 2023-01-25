import requests
import json

from locust import HttpUser, task, between, events
from random import randrange


given_drivers = []


class QuickstartUser(HttpUser):
    host = 'http://localhost:9000'
    wait_time = between(0.5, 1.5)

    def on_start(self):
        # dobavi sve vozace
        self.coordinates = []
        self.chosen_driver = None
        self.current_drive = None
        self.on_station = True
        self.driving_to_start_point = False
        self.driving_the_route = False
        self.driving_to_taxi_stop = False
        self.get_driver()


    def get_driver(self):
        response = self.client.get('/drivers/')
        drivers = response.json()
        if len(drivers) != len(given_drivers):
            rand_idx_driver =  drivers[randrange(0, len(drivers))]
            while rand_idx_driver['id'] in given_drivers:
                rand_idx_driver = drivers[randrange(0, len(drivers))]
            self.chosen_driver = rand_idx_driver
            self.departure = [self.chosen_driver["location"]["latitude"], self.chosen_driver["location"]["longitude"]]
            self.client.put(f"/drivers/set-coordinates/{self.chosen_driver['id']}", json={
                'latitude': self.departure[0],
                'longitude': self.departure[1]
            })
            given_drivers.append(self.chosen_driver["id"])


    @task
    def update_vehicle_coordinates(self):
        if self.chosen_driver is None:
            self.get_driver()
            if self.chosen_driver is None:
                return
        self.chosen_driver = self.client.get(f'/drivers/get-by-id/{self.chosen_driver["id"]}').json()
        if self.on_station:
            self.current_drive = self.client.get(f'/drives/get-paid-drive', json=self.chosen_driver).json()
            if self.current_drive.get('apierror') == None:
                self.destination = [self.current_drive["route"]["waypoints"][0]['latitude'], self.current_drive["route"]["waypoints"][0]['longitude']]
                self.get_new_coordinates() # poslati trenutnu i destinaciju
                self.driving_to_start_point = True
                self.on_station = False

        if len(self.coordinates) > 0:
            if self.driving_to_taxi_stop:
                self.chosen_driver = self.client.get(f"/drivers/get-by-id/{self.chosen_driver['id']}").json()
                if not self.chosen_driver['available']:
                    self.driving_to_start_point = True
                    self.driving_to_taxi_stop = False
                    self.coordinates = []
                    return
            new_coordinate = self.coordinates.pop(0)
            self.client.put(f"/drivers/update-coordinates/{self.chosen_driver['id']}", json={
                'latitude': new_coordinate[0],
                'longitude': new_coordinate[1]
            })

        elif len(self.coordinates) == 0 and self.driving_to_start_point:
            self.current_drive = self.client.get(f'/drives/get-started-drive', json=self.chosen_driver).json()
            if self.current_drive.get('apierror') == None:
                self.departure = self.destination
                self.destination = [self.current_drive["route"]["waypoints"][-1]['latitude'], self.current_drive["route"]["waypoints"][-1]['longitude']]
                self.get_new_coordinates_waypoints(self.current_drive["route"]["waypoints"])
                self.driving_the_route = True
                self.driving_to_start_point = False

            
        elif len(self.coordinates) == 0 and self.driving_the_route:
            self.current_drive = self.client.get(f"/drives/get-ended-drive", json=self.chosen_driver).json()
            if self.current_drive.get('apierror') == None:
                self.chosen_driver = self.client.get(f"/drivers/get-by-id/{self.chosen_driver['id']}").json()
                if self.chosen_driver["available"]:
                    closest_stop = self.client.get(f"/drivers/closest-stop/{self.chosen_driver['id']}").json()
                    self.departure = self.destination
                    self.destination = [closest_stop['latitude'], closest_stop['longitude']]
                    self.get_new_coordinates() # pocetna i krajnja
                    self.driving_to_taxi_stop = True
                    self.driving_the_route = False
                else:
                    self.current_drive = self.client.get(f'/drives/get-paid-drive', json=self.chosen_driver).json()
                    if self.current_drive.get('apierror') == None:
                        self.departure = self.destination
                        self.destination = [self.current_drive["route"]["waypoints"][0]['latitude'], self.current_drive["route"]["waypoints"][0]['longitude']]
                        self.get_new_coordinates() # pocetna i krajnja
                        self.driving_to_start_point = True
                        self.driving_the_route = False

            
        elif len(self.coordinates) == 0 and self.driving_to_taxi_stop:
            self.departure = self.destination
            self.driving_to_taxi_stop = False
            self.on_station = True


    def get_new_coordinates(self):
        response = requests.get(f'https://routing.openstreetmap.de/routed-car/route/v1/driving/{self.departure[1]},{self.departure[0]};{self.destination[1]},{self.destination[0]}?geometries=geojson&overview=false&alternatives=true&steps=true')
        self.routeGeoJSON = response.json()
        self.coordinates = []
        for step in self.routeGeoJSON['routes'][0]['legs'][0]['steps']:
            self.coordinates = [*self.coordinates, *step['geometry']['coordinates']]

    def get_new_coordinates_waypoints(self, waypoints):
        query = ""
        for waypoint in waypoints:
            query += str(waypoint['longitude']) + "," + str(waypoint['latitude']) + ";"
        query = query[:-1]
        response = requests.get(f'https://routing.openstreetmap.de/routed-car/route/v1/driving/{query}?geometries=geojson&overview=false&alternatives=true&steps=true')
        self.routeGeoJSON = response.json()
        self.coordinates = []
        for leg in self.routeGeoJSON['routes'][self.current_drive['route']['routeIdx']]['legs']:
            for step in leg['steps']:
                self.coordinates = [*self.coordinates, *step['geometry']['coordinates']]

