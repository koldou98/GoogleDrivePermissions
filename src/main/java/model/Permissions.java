package model;


import javax.persistence.*;
import java.io.Serializable;

/**
 * Esquema de BBDD de los permisos
 */
@Entity
@Table(name = "Permissions")
public class Permissions implements Serializable {
    private static final long serialVersionUID = 8661212984849230451L;

    @Id
    @Column(name = "permission_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int permission_id;
    @Column(name = "kind")
    private String kind;
    @Column(name = "role")
    private String role;
    @Column(name = "type")
    private String type;
    @Column(name = "allowDiscovery")
    private Boolean allowDiscovery;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private DriveFile driveFile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    public Permissions() {
    }

    public Permissions(String kind, String role, String type, Boolean allowDiscovery,
                       DriveFile driveFile, Users user) {
        this.kind = kind;
        this.role = role;
        this.type = type;
        this.allowDiscovery = allowDiscovery;
        this.driveFile = driveFile;
        this.user = user;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getAllowDiscovery() {
        return allowDiscovery;
    }

    public void setAllowDiscovery(Boolean allowDiscovery) {
        this.allowDiscovery = allowDiscovery;
    }

    public int getPermission_id() {
        return permission_id;
    }

    public void setPermission_id(int permission_id) {
        this.permission_id = permission_id;
    }

    public DriveFile getDriveFile() {
        return driveFile;
    }

    public void setDriveFile(DriveFile driveFile) {
        this.driveFile = driveFile;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Permissions{" +
                "permission_id=" + permission_id +
                ", kind='" + kind + '\'' +
                ", role='" + role + '\'' +
                ", type='" + type + '\'' +
                ", allowDiscovery=" + allowDiscovery +
                ", user=" + user +
                '}';
    }
}
