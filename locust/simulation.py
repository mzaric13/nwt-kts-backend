import requests
import json

from locust import HttpUser, task, between, events
from random import randrange


given_drivers = []



class QuickstartUser(HttpUser):
    host = 'http://localhost:9000'
    wait_time = between(0.5, 2)

    def on_start(self):
        # dobavi sve vozace
        self.chosen_driver = ''
        self.current_drive = None
        self.on_station = True
        self.driving_to_start_point = False
        self.driving_the_route = False
        self.driving_to_taxi_stop = False
        drivers = self.client.get('/drivers')
        if len(drivers) != len(dodeljeni):
            rand_idx_driver =  drivers[randrange(0, len(drivers))]
            while rand_idx_driver.id in dodeljeni:
                rand_idx_driver.id =  drivers[randrange(0, len(drivers))]
            self.chosen_driver = rand_idx_driver
            self.departure = [self.chosen_driver.latitude, self.chosen_driver.longitude]
            dodeljeni.append(self.chosen_driver.id)


    @task
    def update_vehicle_coordinates(self):
        self.chosen_driver = self.client.get(f'/drivers/get-by-id/{self.chosen_driver.id}')
        if self.on_station:
            self.current_drive = self.client.get(f'/drives/get-paid-drive', driver)
            if self.current_drive:
                self.destination = self.current_drive.route.waypoints[-1]
                self.get_new_coordinates() # poslati trenutnu i destinaciju
                self.driving_to_start_point = True
                self.on_station = False
            

        if len(self.coordinates) > 0:
            if self.driving_to_taxi_stop:
                self.chosen_driver = self.client.get(f'/drivers/get-by-id/{self.chosen_driver.id}')
                if not self.chosen_driver.isAvailable:
                    self.driving_to_start_point = True
                    self.driving_to_taxi_stop = False
                    self.coordinates = []
                    return
            self.client.put(f"/drivers/update-coordinates/{self.chosen_driver['id']}", json={
                'latitude': new_coordinate[0],
                'longitude': new_coordinate[1]
            })

        elif len(self.coordinates) == 0 and self.driving_to_start_point:
            self.current_drive = self.client.get(f'/drives/get-started-drive', self.chosen_driver)
            if self.current_drive:
                self.departure = self.destination
                self.destination = self.current_drive.route.waypoints[0]
                self.get_new_coordinates_waypoints(self.current_drive.route.waypoints)
                self.driving_the_route = True
                self.driving_to_start_point = False

            
        elif len(self.coordinates) == 0 and self.driving_the_route:
            self.current_drive = self.client.get(f'/drives/get-ended-drive/{self.current_drive.id}', self.chosen_driver)
            if self.current_drive:
                self.chosen_driver = self.client.get(f'/drivers/get-by-id/{self.chosen_driver.id}')
                if self.chosen_driver.isAvailable:
                    closest_stop = self.client.get(f'/drivers/closest-stop/{self.chosen_driver.id}')
                    self.departure = self.destination
                    self.destination = closest_stop
                    self.get_new_coordinates() # pocetna i krajnja
                    self.driving_to_taxi_stop = True
                    self.driving_the_route = False
                else:
                    self.current_drive = self.client.get(f'/drives/get-paid-drive', driver)
                    self.departure = self.destination
                    self.destination = self.current_drive.route.waypoints[-1]
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
            query += waypoint[1] + "," + waypoint[0] + ";"
        query = query[:-1]
        response = requests.get(f'https://routing.openstreetmap.de/routed-car/route/v1/driving/{query}?geometries=geojson&overview=false&alternatives=true&steps=true')
        self.routeGeoJSON = response.json()
        self.coordinates = []
        for leg in self.routeGeoJSON['routes'][self.current_drive.route.routeIdx]['legs']:
            for step in leg['steps']:
                self.coordinates = [*self.coordinates, *step['geometry']['coordinates']]

