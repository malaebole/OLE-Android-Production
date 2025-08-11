package ae.oleapp.activities;
import static com.android.billingclient.api.BillingClient.ProductType.SUBS;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.window.OnBackInvokedDispatcher;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.PendingPurchasesParams;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryProductDetailsResult;
import com.android.billingclient.api.QueryPurchasesParams;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.PremiumShirtListAdapter;
import ae.oleapp.adapters.ShirtsTeamAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivitySubscriptionBinding;
import ae.oleapp.models.Country;
import ae.oleapp.models.Shirt;
import ae.oleapp.models.SubscriptionCheck;
import ae.oleapp.models.Team;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubscriptionActivity extends BaseActivity implements PurchasesUpdatedListener, View.OnClickListener {
    private ActivitySubscriptionBinding binding;
    private PremiumShirtListAdapter premiumShirtListAdapter;
    private final List<Shirt> shirtList = new ArrayList<>();
    private final List<Team> teamList = new ArrayList<>();
    private final List<Country> countryList = new ArrayList<>();
    private ShirtsTeamAdapter teamAdapter;
    private String selectedCountryId = "";
    private String selectedTeamId = "";
    private String selectedShirtId = "";

    public static final String PREF_FILE= "MyPref";
    //for old playconsole
    // To get key go to Developer Console > Select your app > Development Tools > Services & APIs.
    //for new play console
    //To get key go to Developer Console > Select your app > Monetize > Monetization setup
    //add you key here
    static String base64Key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAu6+S5YXyd2EnFsU0pPGDGE3TKaCd0z1jjy10PnfdKOokrm+GmSch2YsuPh+Jivc3qq67ONwpjToO+FjlfEVB+nRPH65Rm5+hsDEYXk9M/F9EJb7OOWsMNy6JMMhSmLJxJnrVBulGc3Tq0Qfw89KKIcAUpNAr/hlT5b1uBEej5VdT4Qbrtx+UFC7m1EqtDcKfwS7p+Yqs6Q8GN0CuxfbRe/pCuQfke+Qa7NnAeaPNTBgbkVc3FvPiLh7SXebN0SZgDeilIGsyyPYvv7/6QsCmjVdXFHk+YoKUZCA3TY+6RK+/z2F6s1jylH77caZD9TyxqjTg/p+FlEcejNZni4Dv7wIDAQAB";

    //note add unique product ids
    //use same id for preference key
    //static String subscribetestItemID = "test_id";
    static String subscribeItem1ID = "one_month";
    static String subscribeItem2ID = "three_months";
    static String subscribeItem3ID = "six_months";
    static String subscribeItem4ID = "one_year";
    static String selectedSubscribeItemID = "";

    public static ArrayList<String> subcribeItemIDs = new ArrayList<String>() {{
//        add(subscribetestItemID);
        add(subscribeItem1ID);
        add(subscribeItem2ID);
        add(subscribeItem3ID);
        add(subscribeItem4ID);
    }};

    private SharedPreferences getPreferenceObject() {
        return getApplicationContext().getSharedPreferences(PREF_FILE, 0);
    }
    private SharedPreferences.Editor getPreferenceEditObject() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(PREF_FILE, 0);
        return pref.edit();
    }

    //this method is used to get subscribe value from preference
    //to check user subscribe or not
    public boolean getSubscribeItemValueFromPref(String PURCHASE_KEY){
        return getPreferenceObject().getBoolean(PURCHASE_KEY,false);
    }
    //this method is used to set subscribe value to preference
    //when user is subscribed
    //when user is unsubscribed
    private void saveSubscribeItemValueToPref(String PURCHASE_KEY,boolean value){
        getPreferenceEditObject().putBoolean(PURCHASE_KEY,value).commit();
    }


    static String subscribed = " Subscribed";
    static String unsubscribed = " Unsubscribed";
    //static String weeklytest = "Weekly";
    static String onemonth = "1 Month";
    static String threemonths = "3 Months";
    static String sixmonths = "6 Months";
    static String oneyear = "1 Year";


    private void updateTextViews(){

        //TestPlan
//        if(getSubscribeItemValueFromPref(subscribetestItemID)){
//            binding.testText.setText(weeklytest + subscribed);
//            binding.testplan.setBackground(getDrawable(R.drawable.subs_price_bg_active));
//            binding.testPrice.setVisibility(View.GONE);
//        } else{
//            if(!priceSubcribetest.equals("")) {
//                binding.testPrice.setText(priceSubcribetest);
//                binding.testText.setText(weeklytest +unsubscribed);
//                binding.testPrice.setVisibility(View.VISIBLE);
//            }else{
//                binding.testText.setText(weeklytest +unsubscribed);
//            }
//        }
        //Plan1 1 Month

        if(getSubscribeItemValueFromPref(subscribeItem1ID)){
            binding.plan1Text.setText(onemonth + subscribed);
            binding.plan1.setBackground(getDrawable(R.drawable.subs_price_bg_active));
            binding.plan1Price.setVisibility(View.GONE);

        } else{
            if(!priceSubcribe1.equals("")) {
                binding.plan1Price.setText(priceSubcribe1);
                binding.plan1Text.setText(onemonth + unsubscribed);
                binding.plan1Price.setVisibility(View.VISIBLE);
            }else{
                binding.plan1Text.setText(onemonth + unsubscribed);
            }
        }
        //Plan2 3 Months
        if(getSubscribeItemValueFromPref(subscribeItem2ID)){
            binding.plan2Text.setText(threemonths + subscribed);
            binding.plan2.setBackground(getDrawable(R.drawable.subs_price_bg_active));
            binding.plan2Price.setVisibility(View.GONE);
        } else{
            if(!priceSubcribe2.equals("")) {
                binding.plan2Price.setText(priceSubcribe2);
                binding.plan2Text.setText(threemonths + unsubscribed);
                binding.plan2Price.setVisibility(View.VISIBLE);
            }else{
                binding.plan2Text.setText(threemonths + unsubscribed);
            }
        }
        //Plan3 6 Months
        if(getSubscribeItemValueFromPref(subscribeItem3ID)){
            binding.plan3Price.setVisibility(View.GONE);
            binding.plan3Text.setText(sixmonths + subscribed);
        } else{
            if(!priceSubcribe3.equals("")) {
                binding.plan3Price.setVisibility(View.VISIBLE);
                binding.plan3Price.setText(priceSubcribe3);
                binding.plan3Text.setText(sixmonths + unsubscribed);
            }else{
                binding.plan3Text.setText(sixmonths + unsubscribed);
            }
        }

        //Plan4 1 year

        if(getSubscribeItemValueFromPref(subscribeItem4ID)){
            binding.plan4Price.setVisibility(View.GONE);
            binding.plan4Text.setText(oneyear + subscribed);
        } else{
            if(!priceSubcribe4.equals("")) {
                binding.plan4Price.setVisibility(View.VISIBLE);
                binding.plan4Price.setText(priceSubcribe4);
                binding.plan4Text.setText(oneyear + unsubscribed);
            }else{
                binding.plan4Text.setText(oneyear + unsubscribed);
            }
        }

    }

    private BillingClient billingClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubscriptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //makeStatusbarTransperant();

        //Establish connection to billing client
        //check purchase status from google play store cache on every app start
        billingClient = BillingClient.newBuilder(this).enablePendingPurchases(PendingPurchasesParams.newBuilder().build()).setListener(this).build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {

                if(billingResult.getResponseCode()==BillingClient.BillingResponseCode.OK){

                    //fetch item prices
                    querySkuDetails();

                    billingClient.queryPurchasesAsync(
                            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build(),
                            new PurchasesResponseListener() {
                                public void onQueryPurchasesResponse(
                                        @NonNull BillingResult billingResult1,
                                        @NonNull List<Purchase> queryPurchases) {
                                    // Process the result
                                    if(billingResult1.getResponseCode()==BillingClient.BillingResponseCode.OK){
                                        if(queryPurchases.size()>0){
                                            handleSusbcriptionPurchases(queryPurchases);
                                        }
                                        //checking for subscribe items
                                        //check which items are in purchase list and which are not in purchase list
                                        //if items that are found add them to purchaseFound
                                        //check status of found items and save values to preference
                                        //item which are not found simply save false values to their preference
                                        //indexOf return index of item in purchase list from 0-1 (because we have 2 items) else returns -1 if not found
                                        ArrayList<Integer> purchaseFound =new ArrayList<Integer> ();
                                        if(queryPurchases.size()>0){
                                            //check item in purchase list
                                            for(int i=0; i<queryPurchases.size(); i++){
                                                String subcribeItemIDTemp = "";
                                                for(int j=0; j<subcribeItemIDs.size(); j++){
                                                    if(queryPurchases.get(i).getProducts().contains(subcribeItemIDs.get(j))) {
                                                        subcribeItemIDTemp = subcribeItemIDs.get(j);
                                                    }
                                                }

                                                int index=subcribeItemIDs.indexOf(subcribeItemIDTemp);
                                                //if purchase found
                                                if(index>-1)
                                                {
                                                    purchaseFound.add(index);
                                                    //Edhr check hoga token or subscription detail send krni haii
                                                    saveSubscribeItemValueToPref(subcribeItemIDs.get(index), queryPurchases.get(i).getPurchaseState() == Purchase.PurchaseState.PURCHASED);
                                                }
                                            }
                                            //items that are not found in purchase list mark false
                                            //indexOf returns -1 when item is not in foundlist
                                            for(int i=0;i < subcribeItemIDs.size(); i++){
                                                if(!purchaseFound.contains(i)){
                                                    saveSubscribeItemValueToPref(subcribeItemIDs.get(i),false);
                                                }
                                            }
                                        }
                                        //if purchase list is empty that means no item is not purchased/Subscribed
                                        //Or purchase is refunded or canceled
                                        //so mark them all false
                                        else{
                                            for( int k=0; k<subcribeItemIDs.size(); k++ ){
                                                saveSubscribeItemValueToPref(subcribeItemIDs.get(k),false);
                                            }
                                        }
                                    }
                                    else{
                                        SubscriptionActivity.this.runOnUiThread(new Runnable() {
                                            public void run() {
//                                                Toast.makeText(getApplicationContext(),
//                                                        " Error " + billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
                                                Functions.showToast(getApplicationContext(), " Error " + billingResult.getDebugMessage(), FancyToast.ERROR);

                                            }
                                        });
                                    }
                                }
                            }
                    );

                }

            }

            @Override
            public void onBillingServiceDisconnected() {
                SubscriptionActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
//                        Toast.makeText(getApplicationContext(),"Service Disconnected ",Toast.LENGTH_SHORT).show();
                        Functions.showToast(getApplicationContext(), "Service Disconnected ", FancyToast.ERROR);

                    }
                });
            }
        });

        updateTextViews();
        binding.btnClose.setOnClickListener(this);
        //binding.testplan.setOnClickListener(this);
        binding.plan1.setOnClickListener(this);
        binding.plan2.setOnClickListener(this);
        binding.plan3.setOnClickListener(this);
        binding.plan4.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            selectedCountryId = bundle.getString("country_id", "");
            selectedTeamId = bundle.getString("team_id", "");
            selectedShirtId = bundle.getString("shirt_id", "");
        }

        getTeamData(true);

        LinearLayoutManager teamLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.teamRecyclerVu.setLayoutManager(teamLayoutManager);
        teamAdapter = new ShirtsTeamAdapter(getContext(), teamList);
        binding.teamRecyclerVu.setAdapter(teamAdapter);

        CenterZoomLayoutManager shirtLayoutManager = new CenterZoomLayoutManager(getContext(), CenterZoomLayoutManager.HORIZONTAL, false);
        binding.shirtRecyclerVu.setLayoutManager(shirtLayoutManager);
        premiumShirtListAdapter = new PremiumShirtListAdapter(getContext(), shirtList);
        binding.shirtRecyclerVu.setAdapter(premiumShirtListAdapter);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent();
                intent.putExtra("is_added", false);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

    private void getTeamData(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.teamsData(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID),"android");
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject data = object.getJSONObject(Constants.kData);
                            JSONArray country = data.getJSONArray("countries");
                            countryList.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < country.length(); i++) {
                                countryList.add(gson.fromJson(country.get(i).toString(), Country.class));
                            }
                            Country countryData = null;
                            for (int i = 0; i < countryList.size(); i++) {
                                if (countryList.get(i).getId().equalsIgnoreCase(selectedCountryId)) {
                                    countryData = countryList.get(i);
                                    break;
                                }
                            }
                            populateCountryData(countryData);

                        } else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                } else {
                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                } else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }
    private void checkUserSubscription(boolean isLoader, String subscriptionid, String subscriptiontoken, String subscriptionpackage) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.checkUserSubscription(Functions.getAppLang(getContext()), subscriptionid,subscriptiontoken,subscriptionpackage, "google");
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {

                            Intent intent = new Intent();
                            intent.putExtra("is_subscribed", true);
                            setResult(RESULT_OK, intent);
                            finish();

                        } else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                } else {
                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                } else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }

    private void populateCountryData(Country country) {
        if (country == null && countryList.size() > 0) {
            country = countryList.get(0);
        }
        if (country != null) {
            teamList.clear();
            shirtList.clear();
            teamList.addAll(country.getTeams());
            if (selectedTeamId.equalsIgnoreCase("") && teamList.size() > 0) {
                shirtList.clear();
                selectedTeamId = teamList.get(0).getId();
                shirtList.addAll(teamList.get(0).getShirts());
            }
            else {
                for (int i = 0; i < teamList.size(); i++) {
                    if (teamList.get(i).getId().equalsIgnoreCase(selectedTeamId)) {
                        shirtList.clear();
                        shirtList.addAll(teamList.get(i).getShirts());
                        //teamList.get(0).getShirts().get(0).getType().equalsIgnoreCase("paid")
                        break;
                    }
                }
            }
            teamAdapter.setSelectedId(selectedTeamId);
            premiumShirtListAdapter.setSelectedId(selectedShirtId);
        }


    }

    private void onSubscribeClick(String subItemId) {
        if(getSubscribeItemValueFromPref(subItemId)){
            Functions.showToast(getApplicationContext(), "Subscription is Already Subscribed", FancyToast.SUCCESS);

            //selected item is already purchased/subscribed
            return;
        }
        //initiate purchase on selected product/subscribe item click
        //check if service is already connected
        if (billingClient.isReady()) {
            initiatePurchase(subItemId,SUBS);
        }
        //else reconnect service
        else{
            billingClient = BillingClient.newBuilder(SubscriptionActivity.this).enablePendingPurchases(PendingPurchasesParams.newBuilder().build()).setListener(SubscriptionActivity.this).build();
            billingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        initiatePurchase(subItemId,SUBS);
                    } else {
                        SubscriptionActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                //Toast.makeText(getApplicationContext(),"Error "+billingResult.getDebugMessage(),Toast.LENGTH_SHORT).show();
                                Functions.showToast(getApplicationContext(), "Error "+billingResult.getDebugMessage(), FancyToast.ERROR);

                            }
                        });
                    }
                }
                @Override
                public void onBillingServiceDisconnected() {
                    SubscriptionActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
//                            Toast.makeText(getApplicationContext(),"Service Disconnected ",Toast.LENGTH_SHORT).show();
                            Functions.showToast(getApplicationContext(), "Service Disconnected ", FancyToast.ERROR);

                        }
                    });
                }
            });
        }
    }

    private void initiatePurchase(final String PRODUCT_ID,String type) {
        List<QueryProductDetailsParams.Product> queryProductDetailsParamsList = new ArrayList<>();

        QueryProductDetailsParams.Product param1 = QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_ID)
                .setProductType(type)
                .build();

        queryProductDetailsParamsList.add(param1);

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(queryProductDetailsParamsList)
                .build();

        billingClient.queryProductDetailsAsync(params, (billingResult, queryProductDetailsResult) -> {
            // Process the result
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                // Get product details from query result
                List<ProductDetails> productDetailsList = queryProductDetailsResult.getProductDetailsList();

                if (!productDetailsList.isEmpty()) {
                    // in the billing flow creating separate productDetailsParamsList variable
                    List<BillingFlowParams.ProductDetailsParams> productDetailsParamsList = new ArrayList<>();

                    // Retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                    ProductDetails productDetails = productDetailsList.get(0);

                    // Check if subscription offers exist
                    if (productDetails.getSubscriptionOfferDetails() == null ||
                            productDetails.getSubscriptionOfferDetails().isEmpty()) {
                        runOnUiThread(() -> Functions.showToast(
                                SubscriptionActivity.this,
                                "No offers available for " + PRODUCT_ID,
                                FancyToast.ERROR
                        ));
                        return;
                    }

                    // Get the offerToken of the first offer
                    String offerToken = productDetails.getSubscriptionOfferDetails().get(0).getOfferToken();

                    // Set the parameters for the offer that will be presented
                    BillingFlowParams.ProductDetailsParams productDetailsParams =
                            BillingFlowParams.ProductDetailsParams.newBuilder()
                                    .setProductDetails(productDetails)
                                    .setOfferToken(offerToken)
                                    .build();

                    productDetailsParamsList.add(productDetailsParams);

                    BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                            .setProductDetailsParamsList(productDetailsParamsList)
                            .build();

                    // Launch the billing flow
                    BillingResult launchResult = billingClient.launchBillingFlow(
                            SubscriptionActivity.this,
                            billingFlowParams
                    );

                    // Check if launch was successful
                    if (launchResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                        runOnUiThread(() -> Functions.showToast(
                                SubscriptionActivity.this,
                                "Billing error: " + launchResult.getDebugMessage(),
                                FancyToast.ERROR
                        ));
                    }
                } else {
                    // Item not found in Play Store
                    runOnUiThread(() -> Functions.showToast(
                            SubscriptionActivity.this,
                            "Purchase Item " + PRODUCT_ID + " not Found",
                            FancyToast.ERROR
                    ));
                }
            } else {
                // Query failed
                runOnUiThread(() -> Functions.showToast(
                        SubscriptionActivity.this,
                        "Error: " + billingResult.getDebugMessage(),
                        FancyToast.ERROR
                ));
            }
        });


//        billingClient.queryProductDetailsAsync(params, new ProductDetailsResponseListener() {
//            @Override
//            public void onProductDetailsResponse(@NonNull BillingResult billingResult, @NonNull QueryProductDetailsResult queryProductDetailsResult) {
//                // Process the result
//                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
//                    // Get product details from query result
//                    List<ProductDetails> productDetailsList = queryProductDetailsResult.getProductDetailsList();
//                    if (!productDetailsList.isEmpty()) {
//
//                        // in the billing flow creating separate productDetailsParamsList variable
//                        List<BillingFlowParams.ProductDetailsParams> productDetailsParamsList = new ArrayList<>();
//
//                        BillingFlowParams.ProductDetailsParams productDetailsParams = null;
//
//                        // Retrieve a value for "productDetails" by calling queryProductDetailsAsync()
//                        // Get the offerToken of the selected offer
//                        String offerToken = productDetailsList.get(0)
//                                .getSubscriptionOfferDetails().get(0)
//                                .getOfferToken();
//                        // Set the parameters for the offer that will be presented
//                        productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
//                                .setProductDetails(productDetailsList.get(0))
//                                .setOfferToken(offerToken)
//                                .build();
//
//
//                        productDetailsParamsList.add(productDetailsParams);
//
//                        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
//                                .setProductDetailsParamsList(productDetailsParamsList)
//                                .build();
//
//                        // Launch the billing flow
//                        billingClient.launchBillingFlow(SubscriptionActivity.this, billingFlowParams);
//                    } else {
//                        //try to add item/product id in google play console
//
//
//                        SubscriptionActivity.this.runOnUiThread(new Runnable() {
//                            public void run() {
//                                Functions.showToast(getApplicationContext(), "Purchase Item " + PRODUCT_ID + " not Found", FancyToast.ERROR);
//
////                                Toast.makeText(getApplicationContext(), "Purchase Item " + PRODUCT_ID + " not Found", Toast.LENGTH_SHORT).show();
//
//                            }
//                        });
//                    }
//                } else {
//
//
//                    SubscriptionActivity.this.runOnUiThread(new Runnable() {
//                        public void run() {
////                            Toast.makeText(getApplicationContext(), " Error " + billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
//                            Functions.showToast(getApplicationContext(), " Error " + billingResult.getDebugMessage(), FancyToast.ERROR);
//
//                        }
//                    });
//                }
//            }
//
////            public void onProductDetailsResponse(@NonNull BillingResult billingResult, @NonNull List<ProductDetails> productDetailsList) {
////                // Process the result
////                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
////                    if (productDetailsList.size() > 0) {
////
////                        // in the billing flow creating separate productDetailsParamsList variable
////                        List<BillingFlowParams.ProductDetailsParams> productDe  tailsParamsList = new ArrayList<>();
////
////                        BillingFlowParams.ProductDetailsParams productDetailsParams = null;
////
////                        // Retrieve a value for "productDetails" by calling queryProductDetailsAsync()
////                        // Get the offerToken of the selected offer
////                        String offerToken = productDetailsList.get(0)
////                                .getSubscriptionOfferDetails().get(0)
////                                .getOfferToken();
////                        // Set the parameters for the offer that will be presented
////                        productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
////                                .setProductDetails(productDetailsList.get(0))
////                                .setOfferToken(offerToken)
////                                .build();
////
////
////                        productDetailsParamsList.add(productDetailsParams);
////
////                        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
////                                .setProductDetailsParamsList(productDetailsParamsList)
////                                .build();
////
////                        // Launch the billing flow
////                        billingClient.launchBillingFlow(SubscriptionActivity.this, billingFlowParams);
////                    } else {
////                        //try to add item/product id in google play console
////
////
////                        SubscriptionActivity.this.runOnUiThread(new Runnable() {
////                            public void run() {
////                                Functions.showToast(getApplicationContext(), "Purchase Item " + PRODUCT_ID + " not Found", FancyToast.ERROR);
////
//////                                Toast.makeText(getApplicationContext(), "Purchase Item " + PRODUCT_ID + " not Found", Toast.LENGTH_SHORT).show();
////
////                            }
////                        });
////                    }
////                } else {
////
////
////                    SubscriptionActivity.this.runOnUiThread(new Runnable() {
////                        public void run() {
//////                            Toast.makeText(getApplicationContext(), " Error " + billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
////                            Functions.showToast(getApplicationContext(), " Error " + billingResult.getDebugMessage(), FancyToast.ERROR);
////
////                        }
////                    });
////                }
////            }
//        });

    }

    //it is called to check purchase result
    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> purchases) {
        //if item newly purchased
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            handleSusbcriptionPurchases(purchases);
        }
        //if item already purchased then check and reflect changes
        else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {

            billingClient.queryPurchasesAsync(
                    QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build(),
                    new PurchasesResponseListener() {
                        public void onQueryPurchasesResponse(
                                @NonNull BillingResult billingResult1,
                                @NonNull List<Purchase> alreadyPurchases) {
                            // Process the result
                            if(billingResult1.getResponseCode()==BillingClient.BillingResponseCode.OK){
                                if(alreadyPurchases.size()>0){
                                    handleSusbcriptionPurchases(alreadyPurchases);

                                }
                            }
                            else{
                                SubscriptionActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
//                                        Toast.makeText(getApplicationContext(),
//                                                " Error " + billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
                                        Functions.showToast(getApplicationContext(), " Error " + billingResult.getDebugMessage(), FancyToast.ERROR);

                                    }
                                });

                            }
                        }
                    }
            );

        }
        //if purchase cancelled
        else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {

            SubscriptionActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Functions.showToast(getApplicationContext(), "Purchase Canceled", FancyToast.ERROR);

//                    Toast.makeText(getApplicationContext(),"Purchase Canceled",Toast.LENGTH_SHORT).show();
                }
            });
        }
        // Handle any other error msgs
        else {
            SubscriptionActivity.this.runOnUiThread(new Runnable() {
                public void run() {
//                    Toast.makeText(getApplicationContext(),"Error "+billingResult.getDebugMessage(),Toast.LENGTH_SHORT).show();
                    Functions.showToast(getApplicationContext(), "Error "+billingResult.getDebugMessage(), FancyToast.ERROR);
                }
            });


        }
    }

//    void handleSusbcriptionPurchases(List<Purchase>  purchases) {
//        for(int i=0; i<purchases.size(); i++) {
//
//            //handle purchase for subscribe items
//            for(int j=0; j<subcribeItemIDs.size(); j++){
//                //if item is purchased
//                if(purchases.get(i).getProducts().contains(subcribeItemIDs.get(j))){
//                    if (purchases.get(i).getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
//                        if (!verifyValidSignature(purchases.get(i).getOriginalJson(), purchases.get(i).getSignature())) {
//                            // Invalid purchase
//                            // show error to user
//                            SubscriptionActivity.this.runOnUiThread(new Runnable() {
//                                public void run() {
//                                    Toast.makeText(getApplicationContext(), "Error : Invalid Purchase", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//
//                            continue;
//                            //skip current iteration only because other items in purchase list must be checked if present
//                        }
//
//                        if (!purchases.get(i).isAcknowledged()) {
//                            AcknowledgePurchaseParams acknowledgePurchaseParams =
//                                    AcknowledgePurchaseParams.newBuilder()
//                                            .setPurchaseToken(purchases.get(i).getPurchaseToken())
//                                            .build();
//
//                            int finalJ = j;
//                            billingClient.acknowledgePurchase(acknowledgePurchaseParams,
//                                    new AcknowledgePurchaseResponseListener() {
//                                        @Override
//                                        public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
//                                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
//                                                //if purchase is acknowledged
//                                                //then saved value in preference
//                                                SubscriptionActivity.this.runOnUiThread(new Runnable() {
//                                                    public void run() {
//                                                        //new
//                                                        saveSubscribeItemValueToPref(subcribeItemIDs.get(finalJ), true);
//                                                        checkUserSubscription(false,  subcribeItemIDs.get(finalJ),  purchases.get(finalJ).getPurchaseToken(),purchases.get(finalJ).getPackageName());
//                                                        //check
//                                                        Toast.makeText(getApplicationContext(), "Subscribe Item "+subcribeItemIDs.get(finalJ) + " is Subscribed", Toast.LENGTH_SHORT).show();
//                                                        updateTextViews();
//                                                    }
//                                                });
//
//                                            }
//                                        }
//                                    });
//
//                        }
//                        //else item is purchased and also acknowledged
//                        else {
//                            // Grant entitlement to the user on item purchase
//                            if (!getSubscribeItemValueFromPref(subcribeItemIDs.get(j))) {
//                                int finalJ3 = j;
//                                SubscriptionActivity.this.runOnUiThread(new Runnable() {
//                                    public void run() {
//                                        saveSubscribeItemValueToPref(subcribeItemIDs.get(finalJ3), true);
//                                        checkUserSubscription(false,  subcribeItemIDs.get(finalJ3),  purchases.get(finalJ3).getPurchaseToken(),purchases.get(finalJ3).getPackageName()); //old
//                                        //Toast.makeText(getApplicationContext(), "Subscribe Item "+subcribeItemIDs.get(finalJ3) + " is Subscribed.", Toast.LENGTH_SHORT).show();
//                                        updateTextViews();
//                                    }
//                                });
//
//                            }
//                        }
//
//                    }
//                    else if ( purchases.get(i).getPurchaseState() == Purchase.PurchaseState.PENDING) {
//                        int finalJ1 = j;
//                        SubscriptionActivity.this.runOnUiThread(new Runnable() {
//                            public void run() {
//                                Toast.makeText(getApplicationContext(),
//                                        "Subscribe Item "+subcribeItemIDs.get(finalJ1)+" Purchase is Pending. Please complete Transaction", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                    else if (purchases.get(i).getPurchaseState() == Purchase.PurchaseState.UNSPECIFIED_STATE) {
//                        int finalJ2 = j;
//                        SubscriptionActivity.this.runOnUiThread(new Runnable() {
//                            public void run() {
//                                //mark purchase false in case of UNSPECIFIED_STATE
//                                saveSubscribeItemValueToPref(subcribeItemIDs.get(finalJ2),false);
//                                Toast.makeText(getApplicationContext(), "Subscribe Item "+subcribeItemIDs.get(finalJ2)+" Purchase Status Unknown", Toast.LENGTH_SHORT).show();
//                                updateTextViews();
//                            }
//                        });
//
//                    }
//                }
//            }
//        }
//    }

    void handleSusbcriptionPurchases(@NonNull List<Purchase>  purchases) {
        if (purchases.size() == 0) {
            return;
        }
        Purchase purchase = purchases.get(0);
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature())) {
                // Invalid purchase
                // show error to user
//                Toast.makeText(getApplicationContext(), "Error : Invalid Purchase", Toast.LENGTH_SHORT).show();
                SubscriptionActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
//                    Toast.makeText(getApplicationContext(),"Error "+billingResult.getDebugMessage(),Toast.LENGTH_SHORT).show();
                        Functions.showToast(getApplicationContext(), "Error : Invalid Purchase", FancyToast.ERROR);

                    }
                });

                return;
                //skip current iteration only because other items in purchase list must be checked if present
            }

            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams,
                        new AcknowledgePurchaseResponseListener() {
                            @Override
                            public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
                                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                    //if purchase is acknowledged
                                    //then saved value in preference
                                    saveSubscribeItemValueToPref(selectedSubscribeItemID, true);
                                    checkUserSubscription(false,  selectedSubscribeItemID,  purchase.getPurchaseToken(),purchase.getPackageName());
//                                    Toast.makeText(getApplicationContext(), "Subscribe Item "+selectedSubscribeItemID + " is Subscribed", Toast.LENGTH_SHORT).show();
//                                    Toast.makeText(getApplicationContext(), "Subscribed Successfully!", Toast.LENGTH_SHORT).show();
                                    SubscriptionActivity.this.runOnUiThread(new Runnable() {
                                        public void run() {
                                            Functions.showToast(getApplicationContext(), "Subscribed Successfully!", FancyToast.SUCCESS);
                                        }
                                    });

                                    updateTextViews();

                                }
                            }
                        });

            }
            //else item is purchased and also acknowledged
            else {
                // Grant entitlement to the user on item purchase
                if (!getSubscribeItemValueFromPref(selectedSubscribeItemID)) {
                    saveSubscribeItemValueToPref(selectedSubscribeItemID, true);
                    checkUserSubscription(false,  selectedSubscribeItemID,  purchase.getPurchaseToken(),purchase.getPackageName()); //old
                    SubscriptionActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Functions.showToast(getApplicationContext(), "Subscribed Successfully!", FancyToast.SUCCESS);
                        }
                    });
                    //Toast.makeText(getApplicationContext(), "Subscribe Item "+subcribeItemIDs.get(finalJ3) + " is Subscribed.", Toast.LENGTH_SHORT).show();
                    updateTextViews();

                }
            }

        }
        else if (purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {
//            Toast.makeText(getApplicationContext(),
//                    "Subscribe Item "+selectedSubscribeItemID+" Purchase is Pending. Please complete Transaction", Toast.LENGTH_SHORT).show();
            SubscriptionActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Functions.showToast(getApplicationContext(),"Subscribe Item "+selectedSubscribeItemID+" Purchase is Pending. Please complete Transaction",FancyToast.ERROR);
                }
            });
        }
        else if (purchase.getPurchaseState() == Purchase.PurchaseState.UNSPECIFIED_STATE) {
            saveSubscribeItemValueToPref(selectedSubscribeItemID,false);
//            Toast.makeText(getApplicationContext(), "Subscribe Item "+selectedSubscribeItemID+" Purchase Status Unknown", Toast.LENGTH_SHORT).show();
            SubscriptionActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Functions.showToast(getApplicationContext(), "Subscribe Item "+selectedSubscribeItemID+" Purchase Status Unknown", FancyToast.ERROR);
                }
            });

            updateTextViews();

        }
    }

    /**
     * Verifies that the purchase was signed correctly for this developer's public key.
     */
    private boolean verifyValidSignature(String signedData, String signature) {
        try {
            return Security.verifyPurchase(base64Key, signedData, signature);
        } catch (IOException e) {
            return false;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(billingClient!=null){
            billingClient.endConnection();
        }
    }

    //initially prices will be empty and will be filled from Play Store after billing client connection
    public static String priceSubcribetest = "";
    public static String priceSubcribe1 = "";
    public static String priceSubcribe2 = "";
    public static String priceSubcribe3 = "";
    public static String priceSubcribe4 = "";
    private void querySkuDetails() {
        List<QueryProductDetailsParams.Product> queryProductDetailsParamsList = new ArrayList<>();

        for (String subcribeItemID:subcribeItemIDs) {
            QueryProductDetailsParams.Product param1 = QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(subcribeItemID)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build();
            queryProductDetailsParamsList.add(param1);
        }

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(queryProductDetailsParamsList)
                .build();

        //                    public void onProductDetailsResponse(@NonNull BillingResult billingResult, @NonNull List<ProductDetails> productDetailsList) {
//                        // Process the result
//                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
//                            if (productDetailsList.size() > 0) {
//                                for(ProductDetails productDetail:productDetailsList) {
////                                    if(productDetail.getProductId().equals(subscribetestItemID)){
////                                        priceSubcribetest = productDetail.getSubscriptionOfferDetails().get(0)
////                                                .getPricingPhases().getPricingPhaseList().get(0).getFormattedPrice();
////                                    }
////                                    else
//                                    if(productDetail.getProductId().equals(subscribeItem1ID)){
//                                        priceSubcribe1 = productDetail.getSubscriptionOfferDetails().get(0)
//                                                .getPricingPhases().getPricingPhaseList().get(0).getFormattedPrice();
//                                    }
//                                    else if(productDetail.getProductId().equals(subscribeItem2ID)){
//                                        priceSubcribe2 = productDetail.getSubscriptionOfferDetails().get(0)
//                                                .getPricingPhases().getPricingPhaseList().get(0).getFormattedPrice();
//                                    }
//                                    else if(productDetail.getProductId().equals(subscribeItem3ID)){
//                                        priceSubcribe3 = productDetail.getSubscriptionOfferDetails().get(0)
//                                                .getPricingPhases().getPricingPhaseList().get(0).getFormattedPrice();
//                                    }
//                                    else if(productDetail.getProductId().equals(subscribeItem4ID)){
//                                        priceSubcribe4 = productDetail.getSubscriptionOfferDetails().get(0)
//                                                .getPricingPhases().getPricingPhaseList().get(0).getFormattedPrice();
//                                    }
//                                }
//
//                                SubscriptionActivity.this.runOnUiThread(new Runnable() {
//                                    public void run() {
//                                        updateTextViews();
//                                        //here price is filled you can call text updateTextViews method to show item with price
//                                    }
//                                });
//
//                            } else {
//                                //try to add item/product id in google play console
//
//                                SubscriptionActivity.this.runOnUiThread(new Runnable() {
//                                    public void run() {
////                                        Toast.makeText(getApplicationContext(), "Purchase Items not Found", Toast.LENGTH_SHORT).show();
//                                        Functions.showToast(getApplicationContext(), "Purchase Items not Found", FancyToast.ERROR);
//                                    }
//                                });
//
//                            }
//                        } else {
//                            SubscriptionActivity.this.runOnUiThread(new Runnable() {
//                                public void run() {
////                                    Toast.makeText(getApplicationContext(),
////                                            " Error " + billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
//                         Functions.showToast(getApplicationContext(), " Error " + billingResult.getDebugMessage(), FancyToast.ERROR);
//                                }
//                            });
//
//                        }
//                    }
        billingClient.queryProductDetailsAsync(params, (billingResult, queryProductDetailsResult) -> {
            // Process the result
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                // Get the actual product details from the query result
                List<ProductDetails> productDetailsList = queryProductDetailsResult.getProductDetailsList();

                if (!productDetailsList.isEmpty()) {
                    for (ProductDetails productDetail : productDetailsList) {
                        // Check if offers exist before accessing
                        if (productDetail.getSubscriptionOfferDetails() == null ||
                                productDetail.getSubscriptionOfferDetails().isEmpty()) {
                            continue;
                        }

                        // Get the first pricing phase
                        List<ProductDetails.PricingPhase> pricingPhases = productDetail.getSubscriptionOfferDetails()
                                .get(0)
                                .getPricingPhases()
                                .getPricingPhaseList();

                        if (pricingPhases.isEmpty()) {
                            continue;
                        }

                        String formattedPrice = pricingPhases.get(0).getFormattedPrice();
                        String productId = productDetail.getProductId();

                        // Update prices based on product ID
                        if (productId.equals(subscribeItem1ID)) {
                            priceSubcribe1 = formattedPrice;
                        } else if (productId.equals(subscribeItem2ID)) {
                            priceSubcribe2 = formattedPrice;
                        } else if (productId.equals(subscribeItem3ID)) {
                            priceSubcribe3 = formattedPrice;
                        } else if (productId.equals(subscribeItem4ID)) {
                            priceSubcribe4 = formattedPrice;
                        }
                    }

                    // Update UI on main thread
                    // Here price is filled - update UI
                    runOnUiThread(this::updateTextViews);
                } else {
                    // No products found
                    runOnUiThread(() ->
                            Functions.showToast(
                                    getApplicationContext(),
                                    "Purchase Items not Found",
                                    FancyToast.ERROR
                            )
                    );
                }
            } else {
                // Query failed
                runOnUiThread(() ->
                        Functions.showToast(
                                getApplicationContext(),
                                "Error: " + billingResult.getDebugMessage(),
                                FancyToast.ERROR
                        )
                );
            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnClose){
            onBackPressed();
        }
        else if (v == binding.plan1){
            selectedSubscribeItemID = subscribeItem1ID;
            onSubscribeClick(subscribeItem1ID);
//            binding.testplan.setBackground(getDrawable(R.drawable.subs_price_bg));
            binding.plan1.setBackground(getDrawable(R.drawable.subs_price_bg_active));
            binding.plan2.setBackground(getDrawable(R.drawable.subs_price_bg));
            binding.plan3.setBackground(getDrawable(R.drawable.subs_price_bg));
            binding.plan4.setBackground(getDrawable(R.drawable.subs_price_bg));
        }
        else if (v == binding.plan2){
            selectedSubscribeItemID = subscribeItem2ID;
            onSubscribeClick(subscribeItem2ID);
            //binding.testplan.setBackground(getDrawable(R.drawable.subs_price_bg));
            binding.plan1.setBackground(getDrawable(R.drawable.subs_price_bg));
            binding.plan2.setBackground(getDrawable(R.drawable.subs_price_bg_active));
            binding.plan3.setBackground(getDrawable(R.drawable.subs_price_bg));
            binding.plan4.setBackground(getDrawable(R.drawable.subs_price_bg));
        }
        else if (v == binding.plan3){
            selectedSubscribeItemID = subscribeItem3ID;
            onSubscribeClick(subscribeItem3ID);
            //binding.testplan.setBackground(getDrawable(R.drawable.subs_price_bg));
            binding.plan1.setBackground(getDrawable(R.drawable.subs_price_bg));
            binding.plan2.setBackground(getDrawable(R.drawable.subs_price_bg));
            binding.plan3.setBackground(getDrawable(R.drawable.subs_price_bg_active));
            binding.plan4.setBackground(getDrawable(R.drawable.subs_price_bg));
        }
        else if (v == binding.plan4){
            selectedSubscribeItemID = subscribeItem4ID;
            onSubscribeClick(subscribeItem4ID);
            //binding.testplan.setBackground(getDrawable(R.drawable.subs_price_bg));
            binding.plan1.setBackground(getDrawable(R.drawable.subs_price_bg));
            binding.plan2.setBackground(getDrawable(R.drawable.subs_price_bg));
            binding.plan3.setBackground(getDrawable(R.drawable.subs_price_bg));
            binding.plan4.setBackground(getDrawable(R.drawable.subs_price_bg_active));
        }
    }

}

