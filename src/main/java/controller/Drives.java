package controller;

import com.google.api.services.drive.model.TeamDrive;
import com.google.api.services.drive.model.TeamDriveList;
import gui.JMain;
import gui.JMainPane;
import model.DriveUnit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Clase encargada de gestionar las unidades de equipo
 */
public class Drives {

    /**
     * Método que obtiene todos las unidades de equipo a la que pertenece el usuario
     *
     * @return Devuelve una lista con las unidades de equipo (TeamDrive)
     * @throws java.io.IOException Posible excepción
     */
    public static List<TeamDrive> getTeamDrives() throws IOException {
        TeamDriveList tdl = JMainPane.SERVICE.teamdrives().list().execute();
        List<TeamDrive> teamDriveList = new ArrayList<>(tdl.getTeamDrives());
        while (tdl.getNextPageToken() != null && tdl.getNextPageToken().length() > 0) {
            tdl = JMainPane.SERVICE.teamdrives().list().setPageToken(tdl.getNextPageToken()).execute();
            teamDriveList.addAll(tdl.getTeamDrives());
        }
        JMain.log.log(Level.FINE, String.valueOf(teamDriveList.size()));
        return teamDriveList;
    }

    /**
     * Comprueba si la lista introducida coincide con la lista de unidades de equipo de Google Drive
     *
     * @param teamDrives Lista de la unidades de equipo que se quiere comparar
     * @return Si las unidades de equipo son iguales a las de Drive o no. (true o false)
     * @throws IOException Excepción que puede ocurrir durante el proceso.
     */
    public static boolean equalTeamDrives(List<DriveUnit> teamDrives) throws IOException {
        boolean status = false;
        List<String> driveUnitList = new ArrayList<>();
        Drives.getTeamDrives().forEach(teamDrive -> driveUnitList.add(teamDrive.getId()));
        driveUnitList.sort(null);
        List<String> teamDriveList = new ArrayList<>();
        teamDrives.forEach(teamDrive -> teamDriveList.add(teamDrive.getUnit_id()));
        teamDriveList.sort(null);
        if (teamDriveList.equals(driveUnitList)) {
            status = true;
        }
        return status;
    }
}