package model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Esquema de BBDD de los usuarios
 */
@Entity
@Table(name = "User")
public class Users implements Serializable {

    private static final long serialVersionUID = -8541630075260739391L;

    @Id
    @Column(name = "user_id")
    private String permissionId;
    @Column(name = "displayName")
    private String displayName;
    @Column(name = "email")
    private String email;
    @Column(name = "photoLink")
    private String photoLink;
    @OneToMany(mappedBy = "user")
    private List<Permissions> permissions = new ArrayList<>();

    @OneToMany(mappedBy = "owner")
    private List<DriveFile> files = new ArrayList<>();
    public Users() {
    }

    public Users(String permissionId, String displayName, String email, String photoLink) {
        this.permissionId = permissionId;
        this.displayName = displayName;
        this.email = email;
        this.photoLink = photoLink;
    }

    public String getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(String permissionId) {
        this.permissionId = permissionId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoLink() {
        return photoLink;
    }

    public void setPhotoLink(String photoLink) {
        this.photoLink = photoLink;
    }

    public List<Permissions> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permissions> permissions) {
        this.permissions = permissions;
    }

    public List<DriveFile> getFiles() {
        return files;
    }

    public void setFiles(List<DriveFile> files) {
        this.files = files;
    }

    @Override
    public String toString() {
        return "User{" +
                "permissionId='" + permissionId + '\'' +
                ", displayName='" + displayName + '\'' +
                ", email='" + email + '\'' +
                ", photoLink='" + photoLink + '\'' +
                '}';
    }
}
