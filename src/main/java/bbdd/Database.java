package bbdd;

import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.drive.model.TeamDrive;
import controller.Drives;
import controller.Service;
import gui.JMain;
import gui.JMainPane;
import model.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;

/**
 * Clase encargada de gestionar todas las funciones relacionada con la Base de Datos (BBDD).
 */
public class Database {

    public static EntityManagerFactory emf;

    /**
     * Añade o elimina un fichero que se haya modificado desde una ejecución previa
     *
     * @param file Fichero que se va a gestionar
     */
    public static void manageModifiedFiles(File file) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        addUser(em, file);
        DriveUnit driveUnit = (file.getTeamDriveId() == null) ? null : em.find(DriveUnit.class, file.getTeamDriveId());
        if (file.getTrashed()) {
            DriveFile deleteFile = em.find(DriveFile.class, file.getId());
            if (deleteFile != null) {
                em.remove(deleteFile);
            }
        } else {
            addFile(file, em, driveUnit);
        }
        em.getTransaction().commit();
        em.close();
        JMain.log.log(Level.FINE, "Archivos modificado");
    }

    /**
     * Añade el usuario a la tabla {@link Users} y llama al método
     * {@link Database#addFile(File, EntityManager, DriveUnit) Database.addFile para añadir el fichero a la BBDD}
     *
     * @param em   Instancia del Entity Manager
     * @param file Fichero que se va a añadir o modificar en la BBDD
     */
    public static void addFilesToDatabase(EntityManager em, File file) {
        addUser(em, file);
        //* Si se encuentra unidad de equipo, se añade al fichero
        DriveUnit driveUnit = (file.getTeamDriveId() == null) ? null : em.find(DriveUnit.class,
                file.getTeamDriveId());
        addFile(file, em, driveUnit);
    }

    /**
     * @param file      Fichero a almacenar en la BBDD
     * @param em        instancia del Entity Manager
     * @param driveUnit Id de la unidad de equipo
     */
    private static void addFile(File file, EntityManager em, DriveUnit driveUnit) {
        DriveFile df;
        Users owner = (file.getOwners() != null) ? em.find(Users.class,
                file.getOwners().get(0).getPermissionId()) : null;
        if (em.find(DriveFile.class, file.getId()) != null) {
            df = em.find(DriveFile.class, file.getId());
            df = em.merge(df);
            //* Se actualiza el DriveFile de la BBDD. Asi se puede reutilizar el código para las modificaciones.
            df.setFile_name(file.getName());
            df.setMimeType(file.getMimeType());
            df.setOwnedByMe(file.getOwnedByMe());
            df.setShared(file.getShared());
            df.setSize(file.getSize());
            df.setWebViewLink(file.getWebViewLink());
            df.setOwner(owner);
            df.setDriveUnit(driveUnit);
            em.createQuery("delete Permissions where driveFile = '" + file.getId() + "'").executeUpdate();
            em.createQuery("delete FileParents where driveFile = '" + file.getId() + "'").executeUpdate();
        } else {
            df = new DriveFile(file.getId(), file.getName(), file.getMimeType(),
                    file.getOwnedByMe(),
                    file.getShared(), file.getSize(), file.getWebViewLink(), owner,
                    driveUnit);
            em.persist(df);

        }
        addPermissions(em, file, driveUnit, df);
        addParents(em, file, df);
    }

    /**
     * Si no existe, añade un nuevo usuario a la BBDD de la lista de propietarios. Este usuario es de tipo
     * <code>{@link Users}</code>
     *
     * @param em   Instancia del Entity Manager
     * @param file fichero con del que se conseguirán los datos.
     */
    private static void addUser(EntityManager em, File file) {
        if (file.getOwners() != null) {
            file.getOwners().forEach(user -> {
                if (em.find(Users.class, user.getPermissionId()) == null) {
                    Users u = new Users(user.getPermissionId(), user.getDisplayName(), user.getEmailAddress(),
                            user.getPhotoLink());
                    em.persist(u);
                }
            });
        }
    }

    /**
     * Si no existe, añade un nuevo usuario a la BBDD de un permiso. Este usuario es de tipo <code>{@link Users}</code>
     *
     * @param em         Instancia del Entity Manager
     * @param permission permiso del que se obtendrán los datos.
     */
    private static void addUser(EntityManager em, Permission permission) {
        if (em.find(Users.class, permission.getId()) == null) {
            Users u = new Users(permission.getId(), permission.getDisplayName(), permission.getEmailAddress(),
                    permission.getPhotoLink());
            em.persist(u);
        }
    }

    /**
     * Se añaden los permisos de cada fichero a la BBDD. Los permisos son de tipo <code>{@link Permissions}</code>.
     *
     * @param em        Instancia de la entidad
     * @param file      fichero del que se obtendrán los datos
     * @param driveUnit Unidad de equipo a la que pertenece el archivo
     * @param df        objeto que se va a persistir. Este objeto se utilizará para añadir la foreignKey
     */
    private static void addPermissions(EntityManager em, File file, DriveUnit driveUnit, DriveFile df) {
        if (file.getPermissions() != null && driveUnit == null) {
            file.getPermissions().forEach(permission -> {
                addUser(em, permission);
                Users u = em.find(Users.class, permission.getId());
                Permissions p = new Permissions(permission.getKind(),
                        permission.getRole(), permission.getType(), permission.getAllowFileDiscovery(), df, u);
                em.persist(p);
            });
        }
    }

    /**
     * Si el fichero tiene algún directorio padre, se almacena en la BBDD junto al objeto hijo. Este padre es de tipo
     * <code>{@link FileParents}</code>.
     *
     * @param em   Instancia del Entity Manager
     * @param file fichero del que se obtendrán los datos
     * @param df   objeto que se va a persistir. Este objeto se utiliza para añadir la foreignKey.
     */
    private static void addParents(EntityManager em, File file, DriveFile df) {
        if (file.getParents() != null) {
            file.getParents().forEach(parent -> {
                FileParents fp = new FileParents(parent, df);
                em.persist(fp);
            });
        } else if (file.getOwnedByMe()) {
            FileParents fp = new FileParents("SharedParent", df);
            em.persist(fp);
        } else {
            FileParents fp = new FileParents("SharedFolderParent", df);
            em.persist(fp);
        }
    }

    /**
     * Carga la Base en la BBDD las unidades de equipo. Obtiene las unidades de equipo de
     * <code>{@link Drives#getTeamDrives() Drives.getTeamDrives()}</code>. A continuación, para evitar las borra con
     * <code>{@link Database#deleteDriveUnits() Database.deleteDriveUnits()}</code> y seguido los vuelve a añadir
     * utilizando <code>{@link Database#addDriveUnits(List)} ) Database.addTeamDrives(List)}</code>
     *
     * @throws IOException Posible excepción
     */
    public static void addDriveUnits() throws IOException {
        List<TeamDrive> tdl = Drives.getTeamDrives();
        Database.deleteDriveUnits();
        Database.addDriveUnits(tdl);
        JMain.log.log(Level.INFO, "TeamDrive added to Database");
    }

    /**
     * Si no se encuentra una unidad de equipo, la crea. Sino actualizad la unidad de equipo con los últimos cambios.
     *
     * @param teamDrives Lista con las unidades de equipo.
     */
    public static void addDriveUnits(List<TeamDrive> teamDrives) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        List<String> driveUnitList =
                em.createQuery("select unit_id from DriveUnit", String.class).getResultList();
        teamDrives.forEach(teamDrive ->
        {
            if (driveUnitList.contains(teamDrive.getId())) {
                DriveUnit du = em.merge(em.find(DriveUnit.class, teamDrive.getId()));
                du.setName(teamDrive.getName());
                du.setKind(teamDrive.getKind());
            } else {
                DriveUnit du = new DriveUnit(teamDrive.getId(), teamDrive.getKind(), teamDrive.getName());
                em.persist(du);
            }
        });

        em.getTransaction().commit();
        em.close();

    }

    /**
     * Añade en la tabla de ficheros un nuevo fichero 'especial'. Este fichero es la raíz donde se empiezan a almacenar
     * los ficheros de Drive del usuario que lo utiliza.
     *
     * @param em Instancia del Entity Manager
     * @return Objeto del root
     */
    public static DriveFile addRootId(EntityManager em) {
        em.getTransaction().begin();
        String rootId = Service.getRootId(JMainPane.SERVICE);
        DriveFile rootFile = new DriveFile(rootId, "root", null, null, null, null, null, null, null);
        em.persist(rootFile);
        em.getTransaction().commit();
        return rootFile;
    }

    /**
     * Busca en la BBDD el objeto root. Si no está en la base de datos, se llama a la función <code>addRootId
     * </code> que creará el objeto.
     *
     * @return Objeto del root
     */
    public static String getRootId() {
        String rootId;

        EntityManager em = emf.createEntityManager();
        List<String> resultList =
                em.createQuery("select file_id from DriveFile where file_name = 'root' and mimeType = null",
                        String.class).getResultList();
        if (resultList.size() == 0) {
            rootId = addRootId(em).getFile_id();
        } else {
            rootId = resultList.get(0);
        }
        em.close();

        return rootId;
    }

    /**
     * Obtiene los ficheros del usuario. Estos ficheros son del tipo <code>{@link DriveFile}</code>
     *
     * @return Lista de ficheros de tipo {@link DriveFile}
     */
    public static List<DriveFile> getMyDriveFiles() {
        List<DriveFile> list;
        EntityManager em = emf.createEntityManager();
        list = em.createQuery("from DriveFile where ownedByMe = true", DriveFile.class).getResultList();
        em.close();
        return list;
    }

    /**
     * Obtiene los ficheros compartidos de la BBDD. Estos ficheros son de tipo <code>{@link DriveFile}</code>
     *
     * @return Lista con los ficheros de tipo {@link DriveFile}
     */
    public static List<DriveFile> getSharedFiles() {
        List<DriveFile> list;
        EntityManager em = emf.createEntityManager();
        list = em.createQuery("from DriveFile where ownedByMe = false ", DriveFile.class).getResultList();
        em.close();
        return list;
    }

    /**
     * Obtiene los ficheros de todas la unidades de equipo. Estos ficheros son de tipo <code>{@link DriveFile}</code>
     *
     * @return Lista de los ficheros de tipo {@link DriveFile}
     */
    public static List<DriveFile> getAllDriveUnitFiles() {
        List<DriveFile> list;
        EntityManager em = emf.createEntityManager();
        list = em.createQuery("from DriveFile where driveUnit<>null ", DriveFile.class).getResultList();
        em.close();
        return list;
    }

    /**
     * Obtiene la lista de los ficheros de una unidad de equipo. Estos ficheros son de tipo <code>{@link DriveFile}
     * </code>
     *
     * @param unitId Id de la unidad de equipo
     * @return Lista de todos los archivos de la unidad de equipo de tipo <code>{@link DriveFile}</code>
     */
    public static List<DriveFile> getDriveUnitFiles(String unitId) {
        List<DriveFile> list;
        EntityManager em = emf.createEntityManager();
        list =
                em.createQuery("from DriveFile where  driveUnit='" + unitId + "'", DriveFile.class).getResultList();
        em.close();
        return list;
    }

    /**
     * Obtiene la lista con todas las unidades de equipo de la BBDD. La lista es de tipo <code>{@link DriveUnit}</code>
     *
     * @return Lista con las unidades de equipo de tipo <code>{@link DriveUnit}</code>
     */
    public static List<DriveUnit> getTeamDriveList() {
        List<DriveUnit> list;
        EntityManager em = emf.createEntityManager();
        list = em.createQuery("from DriveUnit", DriveUnit.class).getResultList();
        em.close();
        return list;
    }

    /**
     * Borra todas las unidades de equipo de la BBDD
     */
    public static void deleteDriveUnits() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        List<DriveUnit> list = em.createQuery("from DriveUnit", DriveUnit.class).getResultList();
        if (list.size() >= 1) {
            list.forEach(driveUnit -> {
                DriveUnit du = em.find(DriveUnit.class, driveUnit.getUnit_id());
                em.remove(du);
                em.flush();
                em.clear();
            });
        }
        em.getTransaction().commit();
        em.close();
    }

    /**
     * Obtiene los hijos del padre introducido.
     *
     * @param parentId Id del padre
     * @return Lista ordenada alfabéticamente de los ficheros hijos
     */
    public static List<DriveFile> getChildrenFiles(String parentId) {
        EntityManager em = emf.createEntityManager();
        List<DriveFile> fileList =
                em.createQuery("select driveFile from FileParents where parent_id = '" + parentId + "'",
                        DriveFile.class).getResultList();
        fileList.sort(Comparator.comparing(DriveFile::getFile_name));
        em.close();
        return fileList;
    }

    /**
     * Obtiene el propietario del fichero
     *
     * @param id        Id del fichero
     * @param driveUnit id de la unidad de equipo
     * @return Nombre del propietario del fichero
     */
    public static String getOwner(String id, DriveUnit driveUnit) {
        if (id != null && driveUnit == null) {
            EntityManager em = emf.createEntityManager();
            String owner = em.find(DriveFile.class, id).getOwner().getDisplayName();
            em.close();
            return owner;
        } else {
            return "";
        }
    }

    /**
     * Obtiene la lista con los nombres de los usuarios que tienen acceso a ese archivo
     *
     * @param id        Id del fichero
     * @param driveUnit Id de la unidad de equipo
     * @return Lista de los usuarios con los que se tiene compartido un fichero
     */
    public static List<String> getSharedUsers(String id, DriveUnit driveUnit) {
        if (id != null && driveUnit == null) {
            EntityManager em = emf.createEntityManager();
            List<Permissions> permissionsList =
                    em.createQuery("from Permissions where driveFile.file_id = '" + id + "'",
                            Permissions.class).getResultList();
            List<String> userList = new ArrayList<>();

            permissionsList.forEach(permissions -> {
                if (!permissions.getRole().equals("owner") && permissions.getUser().getDisplayName() == null) {
                    switch (permissions.getUser().getPermissionId()) {
                        case "anyone":
                            userList.add("Cualquiera");
                            break;
                        case "anyoneWithLink":
                            userList.add("Cualquiera con enlace");
                            break;
                    }

                } else if (!permissions.getRole().equals("owner")) {
                    if (permissions.getUser().getDisplayName().equals("myOpenDeusto") && permissions.getUser().getEmail() == null) {
                        userList.add("Dominio");
                    } else {
                        userList.add(permissions.getUser().getDisplayName());
                    }
                }
            });
            em.close();
            return userList;
        } else {
            return null;
        }
    }

    /**
     * Obtiene el número de ficheros almacenados en la BBDD
     *
     * @return Número de fichero en la BBDD
     */
    public static long getFileCount() {
        EntityManager em = emf.createEntityManager();
        long count = em.createQuery("SELECT count(file_id) from DriveFile", Long.class).getSingleResult();
        em.close();
        return count;
    }

    /**
     * Obtiene los permisos de un archivo
     *
     * @param id Id del fichero
     * @return Lista de los permisos
     */
    public static List<Permissions> getFilePermissions(String id) {
        EntityManager em = emf.createEntityManager();
        List<Permissions> list =
                em.createQuery("from Permissions where driveFile.file_id = '" + id + "'", Permissions.class).getResultList();
        em.close();
        return list;
    }

    /**
     * Obtiene todos los usuarios y ficheros cuyo nombre, o dirección de correo (en el caso de los usuarios) contenga
     * el texto introducido para buscar.
     *
     * @param text Texto de la búsqueda
     * @return Lista con los ficheros y usuarios que contengan el texto introducido.
     */
    public static List<Object> getSearchData(String text) {
        EntityManager em = emf.createEntityManager();
        List<Object> list = new ArrayList<>();
        list.addAll(em.createQuery("from DriveFile where file_name like '%" + text + "%'", DriveFile.class).getResultList());

        list.addAll(em.createQuery("from Users where displayName like '%" + text + "%' OR email like '%" + text + "%'",
                Users.class).getResultList());
        em.close();
        return list;
    }

    public static List<DriveFile> getUserFiles(String permissionId) {
        EntityManager em = emf.createEntityManager();
        List<DriveFile> fileList =
                new ArrayList<>(em.createQuery("from DriveFile where owner.permissionId = '" + permissionId + "'",
                        DriveFile.class).getResultList());
        (em.createQuery("from Permissions where user.permissionId = '" + permissionId + "'" +
                "and role <> 'owner'", Permissions.class).getResultList())
                .forEach(permissions -> fileList.add(permissions.getDriveFile()));
        return fileList;
    }

    /**
     * Obtiene la lista de ficheros compartidos con el dominio
     *
     * @return Lista de ficheros
     */
    public static List<DriveFile> getDomainSharedFiles() {
        EntityManager em = emf.createEntityManager();
        List<DriveFile> list = em.createQuery("select distinct driveFile from Permissions where type = 'domain'",
                DriveFile.class).getResultList();
        em.close();
        return list;
    }

    /**
     * Obtiene todos los ficheros ficheros compartidos con cualquiera
     *
     * @return Lista de ficheros
     */
    public static List<DriveFile> getAnyoneSharedFiles() {
        EntityManager em = emf.createEntityManager();
        List<DriveFile> list = em.createQuery("select distinct driveFile from Permissions where type = 'anyone'",
                DriveFile.class).getResultList();
        em.close();
        return list;
    }
}
