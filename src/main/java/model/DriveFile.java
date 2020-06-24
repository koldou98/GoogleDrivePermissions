package model;


import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad de la BBDD que almacenará los ficheros de GoogleDrive
 */
@Entity
@Table(name = "DriveFile")
public class DriveFile implements Serializable {

    private static final long serialVersionUID = 2187223246344363865L;

    @Id
    @Column(name = "file_id")
    private String file_id;
    @Column(name = "file_name")
    private String file_name;
    @Column(name = "mimeType")
    private String mimeType;
    @Column(name = "ownedByMe")
    private Boolean ownedByMe;
    @Column(name = "shared")
    private Boolean shared;
    @Column(name = "size")
    private Long size;
    @Column(name = "webViewLink")
    private String webViewLink;

    @OneToMany(mappedBy = "driveFile", cascade = CascadeType.ALL)
    private List<FileParents> parents;

    @OneToMany(mappedBy = "driveFile", cascade = CascadeType.ALL)
    private List<Permissions> permissions = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id")
    private DriveUnit driveUnit;

    public DriveFile() {
    }

    /**
     * Constructor completo de DriveFile
     *
     * @param file_id Identificador único del fichero
     * @param file_name Nombre del fichero
     * @param mimeType Tipo del fichero
     * @param ownedByMe El fichero pertenece al usuario o no
     * @param shared El fichero está compartido o no
     * @param size Tamaño del fichero
     * @param webViewLink Link de visualización en la web del fichero
     * @param owner Propietario del fichero
     * @param driveUnit Unidad de equipo del fichero, puede ser <code>null</code>
     */
    public DriveFile(String file_id, String file_name, String mimeType, Boolean ownedByMe, Boolean shared, Long size
            , String webViewLink, Users owner, DriveUnit driveUnit) {
        this.file_id = file_id;
        this.file_name = file_name;
        this.mimeType = mimeType;
        this.ownedByMe = ownedByMe;
        this.shared = shared;
        this.size = size;
        this.webViewLink = webViewLink;
        this.owner = owner;
        this.driveUnit = driveUnit;
    }
    /**
     * Constructor reducido de <code>{@link DriveFile}</code>. Este constructor únicamente se ha de emplear para tener
     * objetos
     * pertenecientes a la clase DriveFile, pero no para almacenar dicho objeto en la BD.
     *
     * @param file_name Nombre del fichero
     * @param mimeType Tipo del fichero
     */
    public DriveFile(String file_name, String mimeType) {
        this.file_name = file_name;
        this.mimeType = mimeType;
    }

    public String getFile_id() {
        return file_id;
    }

    public void setFile_id(String file_id) {
        this.file_id = file_id;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Boolean getOwnedByMe() {
        return ownedByMe;
    }

    public void setOwnedByMe(Boolean ownedByMe) {
        this.ownedByMe = ownedByMe;
    }

    public Boolean getShared() {
        return shared;
    }

    public void setShared(Boolean shared) {
        this.shared = shared;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getWebViewLink() {
        return webViewLink;
    }

    public void setWebViewLink(String webViewLink) {
        this.webViewLink = webViewLink;
    }

    public List<FileParents> getParents() {
        return parents;
    }

    public void setParents(List<FileParents> parents) {
        this.parents = parents;
    }

    public List<Permissions> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permissions> permissions) {
        this.permissions = permissions;
    }

    public Users getOwner() {
        return owner;
    }

    public void setOwner(Users owner) {
        this.owner = owner;
    }

    public DriveUnit getDriveUnit() {
        return driveUnit;
    }

    public void setDriveUnit(DriveUnit driveUnit) {
        this.driveUnit = driveUnit;
    }

    @Override
    public String toString() {
        return "DriveFile{" +
                "file_id='" + file_id + '\'' +
                ", file_name='" + file_name + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", ownedByMe=" + ownedByMe +
                ", shared=" + shared +
                ", size=" + size +
                ", webViewLink='" + webViewLink + '\'' +
                ", owner='" + owner + '\'' +
                ", driveUnit=" + driveUnit +
                '}';
    }
}