package nwt.kts.backend.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Collection;

@Entity
@Table(name = "users")
@Inheritance(strategy=InheritanceType.JOINED)
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    protected Integer id;

    @Column(name = "email", unique = true, nullable = false)
    protected String email;

    @Column(name = "phone_number", unique = true)
    protected String phoneNumber;

    @Column(name = "password")
    protected String password;

    @Column(name = "name", nullable = false)
    protected String name;

    @Column(name = "surname", nullable = false)
    protected String surname;

    @Column(name = "city")
    protected String city;

    @Column(name = "picture")
    protected String picture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    protected Role role;

    @Column(name = "provider", nullable = false)
    protected Provider provider;

    public User() {

    }

    public User(Integer id, String email, String phoneNumber, String password, String name, String surname, String city, Role role) {
        this.id = id;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.city = city;
        this.role = role;
    }

    public User(String email, String phoneNumber, String password, String name, String surname, String city, Role role) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.city = city;
        this.role = role;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<Role> collection = Arrays.asList(this.role);
        return collection;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // TODO: proveriti ovo
    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String profilePicture) {
        this.picture = profilePicture;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }
}
