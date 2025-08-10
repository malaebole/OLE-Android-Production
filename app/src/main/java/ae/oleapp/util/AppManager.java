package ae.oleapp.util;

import java.util.ArrayList;
import java.util.List;

import ae.oleapp.api.APIClient;
import ae.oleapp.api.APIInterface;
import ae.oleapp.models.Club;
import ae.oleapp.models.OleClubFacility;
import ae.oleapp.models.OleCountry;
import ae.oleapp.models.OleFieldData;

public class AppManager {
    private static final AppManager ourInstance = new AppManager();
    public static AppManager getInstance() {
        return ourInstance;
    }

    public APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
    public APIInterface apiInterface2 = APIClient.getClient2().create(APIInterface.class);
    public APIInterface apiInterfaceNode = APIClient.getNodeClient().create(APIInterface.class);

    public List<OleCountry> countries = new ArrayList<>();
    public List<Club> clubs = new ArrayList<>();
    public List<OleClubFacility> clubFacilities = new ArrayList<>();
    public OleFieldData oleFieldData = null;
    public int notificationCount = 0;

    private AppManager() {
    }
}
