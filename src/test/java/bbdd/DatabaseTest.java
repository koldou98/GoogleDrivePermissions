package bbdd;

import model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DatabaseTest {

    private static Users u1;
    private static Users u2;
    private static Users domain;
    private static Users anyone;
    private static Users anyoneWithLink;
    private static DriveUnit du1;
    private static DriveUnit du2;
    private static DriveFile root;
    private static DriveFile df1;
    private static DriveFile df2;
    private static DriveFile df3;
    private static DriveFile df4;
    private static DriveFile df5;
    private static Permissions p1;
    private static Permissions p2;
    private static Permissions p3;
    private static Permissions p4;
    private static Permissions p5;
    private static FileParents fp1;

    @Before
    public void setUp() throws Exception {
        Database.emf = Persistence.createEntityManagerFactory("testDB");
        EntityManager em = Database.emf.createEntityManager();
        em.getTransaction().begin();
        u1 = new Users("1", "user1", "emailUser1", "linkUser1");
        u2 = new Users("2", "user2", "emailUser2", "linkUser2");
        domain = new Users("domain", "myOpenDeusto", null, null);
        anyone = new Users("anyone", null, null, null);
        anyoneWithLink = new Users("anyoneWithLink", null, null, null);
        em.persist(u1);
        em.persist(u2);
        em.persist(domain);
        em.persist(anyone);
        em.persist(anyoneWithLink);
        du1 = new DriveUnit("11", "du", "du1");
        du2 = new DriveUnit("12", "du", "du2");
        em.persist(du1);
        em.persist(du2);
        root = new DriveFile("rootId", "root", null, null, null, null, null, null, null);
        df1 = new DriveFile("21", "file1", "tipo1", true, true, 10L, "linkdf1", u1, null);
        df2 = new DriveFile("22", "file2", "tipo2", false, true, 10L, "linkdf2", u2, null);
        df3 = new DriveFile("23", "file3", "tipo3", null, null, 10L, "linkdf3", null, du1);
        df4 = new DriveFile("24", "file4", "tipo4", null, null, 10L, "linkdf4", null, du2);
        df5 = new DriveFile("25", "file5", "tipo5", true, false, 10L, "linkdf5", u1, null);
        em.persist(root);
        em.persist(df1);
        em.persist(df2);
        em.persist(df3);
        em.persist(df4);
        em.persist(df5);
        fp1 = new FileParents("21", df5);
        em.persist(fp1);
        p1 = new Permissions("perm", "owner", "user", null, df1, u1);
        p2 = new Permissions("perm", "reader", "user", null, df1, u2);
        p3 = new Permissions("perm", "writer", "domain", true, df1, domain);
        p4 = new Permissions("perm", "reader", "anyone", true, df1, anyone);
        p5 = new Permissions("perm", "commenter", "anyone", true, df1, anyoneWithLink);
        em.persist(p1);
        em.persist(p2);
        em.persist(p3);
        em.persist(p4);
        em.persist(p5);
        em.getTransaction().commit();
        em.close();
    }

    @After
    public void tearDown() throws Exception {
        EntityManager em = Database.emf.createEntityManager();
        em.getTransaction().begin();
        em.createQuery("delete Permissions").executeUpdate();
        em.getTransaction().commit();
        em.close();
        Database.emf.close();
    }

    @Test
    public void getRootId() {
        String rootId = Database.getRootId();
        assertEquals("rootId", rootId);
    }


    @Test
    public void getMyDriveFiles() {
        List<DriveFile> list = Database.getMyDriveFiles();
        List<DriveFile> listok = new ArrayList<>();
        listok.add(df1);
        listok.add(df5);
        assertEquals(listok.toString(), list.toString());
    }

    @Test
    public void getSharedFiles() {
        List<DriveFile> list = Database.getSharedFiles();
        List<DriveFile> listok = new ArrayList<>();
        listok.add(df2);
        assertEquals(listok.toString(), list.toString());
    }

    @Test
    public void getAllDriveUnitFiles() {
        List<DriveFile> list = Database.getAllDriveUnitFiles();
        List<DriveFile> listok = new ArrayList<>();
        listok.add(df3);
        listok.add(df4);
        assertEquals(listok.toString(), list.toString());
    }

    @Test
    public void getDriveUnitFiles() {
        List<DriveFile> list = Database.getDriveUnitFiles("11");
        List<DriveFile> listok = new ArrayList<>();
        listok.add(df3);
        assertEquals(listok.toString(), list.toString());
    }

    @Test
    public void getTeamDriveList() {
        List<DriveUnit> list = Database.getTeamDriveList();
        List<DriveUnit> listok = new ArrayList<>();
        listok.add(du1);
        listok.add(du2);
        assertEquals(listok.toString(), list.toString());
    }

    @Test
    public void deleteDriveUnits() {
        Database.deleteDriveUnits();
        List<DriveUnit> list = Database.getTeamDriveList();
        assertEquals(0, list.size());
    }

    @Test
    public void getChildrenFiles() {
        List<DriveFile> list = Database.getChildrenFiles("21");
        List<DriveFile> listok = new ArrayList<>();
        listok.add(df5);
        assertEquals(listok.toString(), list.toString());
    }

    @Test
    public void getOwner() {
        String owner = Database.getOwner(df1.getFile_id(), df1.getDriveUnit());
        assertEquals("user1", owner);
    }

    @Test
    public void getNoOwner() {
        String owner = Database.getOwner(df3.getFile_id(), df3.getDriveUnit());
        assertEquals("", owner);
    }

    @Test
    public void getSharedUsers() {
        List<String> users = Database.getSharedUsers(df1.getFile_id(), df1.getDriveUnit());
        List<String> usersok = new ArrayList<>();
        usersok.add(u2.getDisplayName());
        usersok.add("Dominio");
        usersok.add("Cualquiera");
        usersok.add("Cualquiera con enlace");
        assert users != null;
        assertEquals(usersok.toString(), users.toString());
    }

    @Test
    public void getNoSharedUsers() {
        List<String> users = Database.getSharedUsers(df3.getFile_id(), df3.getDriveUnit());
        assertNull(users);
    }

    @Test
    public void getFileCount() {
        long number = Database.getFileCount();
        assertEquals(6L, number);
    }

    @Test
    public void getFilePermissions() {
        List<Permissions> list = Database.getFilePermissions(df1.getFile_id());
        List<Permissions> listok = new ArrayList<>();
        listok.add(p1);
        listok.add(p2);
        listok.add(p3);
        listok.add(p4);
        listok.add(p5);
        assertEquals(listok.toString(), list.toString());
    }

    @Test
    public void getSearchData() {
        List<Object> list = Database.getSearchData("1");
        List<Object> listok = new ArrayList<>();
        listok.add(df1);
        listok.add(u1);
        assertEquals(listok.toString(), list.toString());
    }

    @Test
    public void getUserFiles() {
        List<DriveFile> list = Database.getUserFiles(u1.getPermissionId());
        List<DriveFile> listok = new ArrayList<>();
        listok.add(df1);
        listok.add(df5);
        assertEquals(listok.toString(), list.toString());
    }

    @Test
    public void getDomainSharedFiles() {
        List<DriveFile> list = Database.getDomainSharedFiles();
        List<DriveFile> listok = new ArrayList<>();
        listok.add(df1);
        assertEquals(listok.toString(), list.toString());
    }

    @Test
    public void getAnyoneSharedFiles() {
        List<DriveFile> list = Database.getAnyoneSharedFiles();
        List<DriveFile> listok = new ArrayList<>();
        listok.add(df1);
        assertEquals(listok.toString(), list.toString());
    }
}