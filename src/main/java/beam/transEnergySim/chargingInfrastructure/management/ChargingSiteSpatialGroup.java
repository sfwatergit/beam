package beam.transEnergySim.chargingInfrastructure.management;

/**
 * @Author mygreencar.
 */
public interface ChargingSiteSpatialGroup {
    String getName();
    double getChargingLoadInKw(int chargerType);
    double getNumPluggedIn(int chargerType);
    void addChargingLoadInKw(int chargerType, double chargingPowerInKw);
    void addNumPluggedIn(int chargerType, int num);
}