package model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Entidad de la BBDD que almacenará las relaciones de los padres y los ficheros.
 */
@Entity
@Table(name = "FileParents")
public class FileParents implements Serializable {

    private static final long serialVersionUID = -563247808872193762L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "parent_id")
    private String parent_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private DriveFile driveFile;

    public FileParents() {
    }

    public FileParents(String parent_id, DriveFile driveFile) {
        this.parent_id = parent_id;
        this.driveFile = driveFile;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public DriveFile getDriveFile() {
        return driveFile;
    }

    public void setDriveFile(DriveFile driveFile) {
        this.driveFile = driveFile;
    }

    @Override
    public String toString() {
        return "FileParents{" +
                "id=" + id +
                ", parent_id='" + parent_id + '\'' +
                ", driveFile=" + driveFile +
                '}';
    }
}
