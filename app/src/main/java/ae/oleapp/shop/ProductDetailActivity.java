package ae.oleapp.shop;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.stfalcon.frescoimageviewer.ImageViewer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.adapters.OleProductColorAdapter;
import ae.oleapp.adapters.OleProductListAdapter;
import ae.oleapp.adapters.OleProductReviewsAdapter;
import ae.oleapp.adapters.OleProductSpecsAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityProductDetailBinding;
import ae.oleapp.dialogs.OleAddedCartDialogFragment;
import ae.oleapp.dialogs.OleSelectionListDialog;
import ae.oleapp.models.OleAttributeCombination;
import ae.oleapp.models.OleChoiceOption;
import ae.oleapp.models.OleDeliveryCity;
import ae.oleapp.models.Product;
import ae.oleapp.models.OleProductColor;
import ae.oleapp.models.OleProductReview;
import ae.oleapp.models.OleProductSpecs;
import ae.oleapp.models.OleProductVariant;
import ae.oleapp.models.OleSelectionList;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import ae.oleapp.util.OleVariantView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends BaseActivity implements View.OnClickListener {

    private ActivityProductDetailBinding binding;
    private String productId = "";
    private String selectedCombinationId = "";
    private String selectedColor = "";
    private int selectedQty = 1;
    private int currentStock = 0;
    private Product productDetail;
    private final List<OleProductColor> colorList = new ArrayList<>();
    private final List<OleProductReview> reviewList = new ArrayList<>();
    private final List<OleProductSpecs> specsList = new ArrayList<>();
    private final List<Product> relatedProducts = new ArrayList<>();
    private OleProductColorAdapter colorAdapter;
    private final List<OleProductVariant> selectedOptions = new ArrayList<>();
    private OleProductSpecsAdapter specsAdapter;
    private OleProductReviewsAdapter reviewsAdapter;
    private OleProductListAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.details);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            productId = bundle.getString("prod_id", "");
        }

        binding.detailVu.setVisibility(View.VISIBLE);
        binding.specsRecyclerVu.setVisibility(View.GONE);
        binding.reviewsRecyclerVu.setVisibility(View.GONE);
        binding.scrollVu.setVisibility(View.INVISIBLE);
        binding.whatsappVu.setVisibility(View.INVISIBLE);
        binding.tabLayout.setVisibility(View.INVISIBLE);

        LinearLayoutManager colorLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        binding.colorRecyclerVu.setLayoutManager(colorLayoutManager);
        colorAdapter = new OleProductColorAdapter(getContext(), colorList);
        colorAdapter.setItemClickListener(colorClicked);
        binding.colorRecyclerVu.setAdapter(colorAdapter);

        LinearLayoutManager specsLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.specsRecyclerVu.setLayoutManager(specsLayoutManager);
        specsAdapter = new OleProductSpecsAdapter(getContext(), specsList);
        binding.specsRecyclerVu.setAdapter(specsAdapter);

        LinearLayoutManager reviewsLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.reviewsRecyclerVu.setLayoutManager(reviewsLayoutManager);
        reviewsAdapter = new OleProductReviewsAdapter(getContext(), reviewList);
        binding.reviewsRecyclerVu.setAdapter(reviewsAdapter);

        LinearLayoutManager relatedLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.relatedRecyclerVu.setLayoutManager(relatedLayoutManager);
        productAdapter = new OleProductListAdapter(getContext(), relatedProducts, true);
        productAdapter.setItemClickListener(productClicked);
        binding.relatedRecyclerVu.setAdapter(productAdapter);

        getProductAPI(true);

        binding.bar.backBtn.setOnClickListener(this);
        binding.btnPlus.setOnClickListener(this);
        binding.btnMinus.setOnClickListener(this);
        binding.deliveryVu.setOnClickListener(this);
        binding.whatsappVu.setOnClickListener(this);
        binding.btnFav.setOnClickListener(this);
        binding.btnShare.setOnClickListener(this);
        binding.btnAddCart.setOnClickListener(this);

    }

    TabLayout.OnTabSelectedListener tabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            if (productDetail.getSpecifications().isEmpty()) {
                if (tab.getPosition() == 0) {
                    binding.detailVu.setVisibility(View.VISIBLE);
                    binding.specsRecyclerVu.setVisibility(View.GONE);
                    binding.reviewsRecyclerVu.setVisibility(View.GONE);
                } else {
                    binding.detailVu.setVisibility(View.GONE);
                    binding.specsRecyclerVu.setVisibility(View.GONE);
                    binding.reviewsRecyclerVu.setVisibility(View.VISIBLE);
                }
            }
            else {
                if (tab.getPosition() == 0) {
                    binding.detailVu.setVisibility(View.VISIBLE);
                    binding.specsRecyclerVu.setVisibility(View.GONE);
                    binding.reviewsRecyclerVu.setVisibility(View.GONE);
                } else if (tab.getPosition() == 1) {
                    binding.detailVu.setVisibility(View.GONE);
                    binding.specsRecyclerVu.setVisibility(View.VISIBLE);
                    binding.reviewsRecyclerVu.setVisibility(View.GONE);
                } else {
                    binding.detailVu.setVisibility(View.GONE);
                    binding.specsRecyclerVu.setVisibility(View.GONE);
                    binding.reviewsRecyclerVu.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    OleProductColorAdapter.ItemClickListener colorClicked = new OleProductColorAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            colorAdapter.setSelectedColorIndex(pos);
            OleProductColor oleProductColor = colorList.get(pos);
            selectedColor = oleProductColor.getCode();
            int index = findFromSelectedOptions("-1");
            if (index != -1) {
                selectedOptions.get(index).setValue(oleProductColor.getName());
                selectedOptions.get(index).setTitle(oleProductColor.getCode());
            }
            else {
                OleProductVariant variant = new OleProductVariant(0, "-1", oleProductColor.getCode(), oleProductColor.getName());
                selectedOptions.add(variant);
            }
            setVariantPrice();
        }
    };

    OleProductListAdapter.ItemClickListener productClicked = new OleProductListAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            Intent intent = new Intent(getContext(), ProductDetailActivity.class);
            intent.putExtra("prod_id", relatedProducts.get(pos).getId());
            startActivity(intent);
        }

        @Override
        public void favClicked(View view, int pos) {
            if (!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
                Functions.showToast(getContext(), getString(R.string.please_login_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                return;
            }
            Product product = relatedProducts.get(pos);
            if (product.getIsFavorite().equalsIgnoreCase("1")) {
                removeFromWishlist(true, product.getId(), pos);
            }
            else {
                addToWishlist(true, product.getId(), pos);
            }
        }
    };

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
        else if (v == binding.btnPlus) {
            plusClicked();
        }
        else if (v == binding.btnMinus) {
            minusClicked();
        }
        else if (v == binding.deliveryVu) {
            deliveryVuClicked();
        }
        else if (v == binding.whatsappVu) {
            whatsappClicked();
        }
        else if (v == binding.btnFav) {
            favClicked();
        }
        else if (v == binding.btnShare) {
            shareClicked();
        }
        else if (v == binding.btnAddCart) {
            addCartClicked();
        }
    }

    private void plusClicked() {
        int totalOption = 0;
        if (productDetail.getColors().isEmpty()) {
            totalOption = productDetail.getChoiceOptions().size();
        }
        else {
            totalOption = productDetail.getChoiceOptions().size() + 1;
        }
        if (productDetail.getVariantProduct().equalsIgnoreCase("1") && selectedOptions.size() != totalOption) {
            Functions.showToast(getContext(), getString(R.string.select_product_variant), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
            return;
        }
        if (selectedQty < currentStock) {
            selectedQty += 1;
            binding.tvQty.setText(String.valueOf(selectedQty));
        }
    }

    private void minusClicked() {
        if (selectedQty > 1) {
            selectedQty -= 1;
            binding.tvQty.setText(String.valueOf(selectedQty));
        }
    }

    private void deliveryVuClicked() {
        if (productDetail != null && productDetail.getDeliveryData().size() > 0) {
            List<OleSelectionList> oleSelectionList = new ArrayList<>();
            for (int i = 0; i < productDetail.getDeliveryData().size(); i++) {
                oleSelectionList.add(new OleSelectionList(String.valueOf(i), productDetail.getDeliveryData().get(i).getCityName()));
            }
            OleSelectionListDialog dialog = new OleSelectionListDialog(getContext(), getString(R.string.select_city), false);
            dialog.setLists(oleSelectionList);
            dialog.setOnItemSelected(new OleSelectionListDialog.OnItemSelected() {
                @Override
                public void selectedItem(List<OleSelectionList> selectedItems) {
                    OleSelectionList item = selectedItems.get(0);
                    binding.tvCity.setText(item.getValue());
                    OleDeliveryCity oleDeliveryCity = productDetail.getDeliveryData().get(Integer.parseInt(item.getId()));
                    binding.tvDate.setText(oleDeliveryCity.getDeliveryDate());
                    binding.tvOrderIn.setText(getString(R.string.order_in_place, oleDeliveryCity.getOrderIn()));
                }
            });
            dialog.show();
        }
    }

    private void whatsappClicked() {
        String url = "https://api.whatsapp.com/send?phone=+971547215551";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    private void favClicked() {
        if (!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
            Functions.showToast(getContext(), getString(R.string.please_login_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
            return;
        }
        if (productDetail != null) {
            if (productDetail.getIsFavorite().equalsIgnoreCase("1")) {
                removeFromWishlist(true, productDetail.getId(), -1);
            } else {
                addToWishlist(true, productDetail.getId(), -1);
            }
        }
    }

    private void shareClicked() {
        if (productDetail != null) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, String.format("Check out this item %s", productDetail.getShareLink()));
            startActivity(Intent.createChooser(intent, "Share"));
        }
    }

    private void addCartClicked() {
        if (productDetail == null) {
            return;
        }
        if (!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
            Functions.showToast(getContext(), getString(R.string.please_login_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
            return;
        }
        int totalOption = 0;
        if (productDetail.getColors().isEmpty()) {
            totalOption = productDetail.getChoiceOptions().size();
        }
        else {
            totalOption = productDetail.getChoiceOptions().size() + 1;
        }
        if (productDetail.getVariantProduct().equalsIgnoreCase("1") && selectedOptions.size() != totalOption) {
            Functions.showToast(getContext(), getString(R.string.select_product_variant), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
            return;
        }
        if (selectedQty <= currentStock) {
            String variantStr = "";
            try {
                JSONArray array = new JSONArray();
                for (OleProductVariant variant : selectedOptions) {
                    if (!variant.getId().equalsIgnoreCase("-1")) {
                        JSONObject object = new JSONObject();
                        object.put("id", variant.getId());
                        object.put("value", variant.getValue());
                        array.put(object);
                    }
                }
                variantStr = array.toString();
                addCartAPI(true, variantStr);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            Functions.showToast(getContext(), getString(R.string.selected_qty_more_than_stock), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
        }
    }

    private void populateData() {
        if (productDetail == null) {
            return;
        }
        if (productDetail.getSpecifications().isEmpty()) {
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.details));
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.reviews));
        }
        else {
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.details));
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.specifications));
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.reviews));
        }
        binding.scrollVu.setVisibility(View.VISIBLE);
        binding.whatsappVu.setVisibility(View.VISIBLE);
        binding.tabLayout.setVisibility(View.VISIBLE);
        binding.tabLayout.addOnTabSelectedListener(tabSelectedListener);
        setupSlider(productDetail.getPhotos());
        binding.tvBrandName.setText(productDetail.getBrand().getName());
        binding.tvSold.setText(getString(R.string.sold_place, productDetail.getSold()));
        binding.tvItemNo.setText(getString(R.string.item_no_place, productDetail.getProductNumber()));
        binding.tvName.setText(productDetail.getName());
        binding.tvPrice.setText(String.format("%s %s", productDetail.getSalePrice(), productDetail.getCurrency()));
        setActualPrice(productDetail.getDiscount(), productDetail.getSalePrice(), productDetail.getDiscountType(), productDetail.getCurrency());
        if (productDetail.getIsFavorite().equalsIgnoreCase("1")) {
            binding.btnFav.setImageResource(R.drawable.shop_fav_ic);
        }
        else {
            binding.btnFav.setImageResource(R.drawable.shop_unfav_ic);
        }
        if (Float.parseFloat(productDetail.getRating()) > 0) {
            binding.tvRating.setText(productDetail.getRating());
        }
        else {
            binding.tvRating.setText("");
        }
        binding.tvRateCount.setText(getString(R.string.reviews_place, productDetail.getReviewsCount()));
        binding.ratingBar.setStar(Float.parseFloat(productDetail.getRating()));
        if (productDetail.getVariantProduct().equalsIgnoreCase("1")) {
            binding.tvStock.setText("");
            if (productDetail.getColors().size() > 0) {
                binding.colorVu.setVisibility(View.VISIBLE);
                colorList.clear();
                colorList.addAll(productDetail.getColors());
                colorAdapter.notifyDataSetChanged();
            }
            else {
                binding.colorVu.setVisibility(View.GONE);
            }
            for (int i = 0; i < productDetail.getChoiceOptions().size(); i++) {
                OleChoiceOption option = productDetail.getChoiceOptions().get(i);
                OleVariantView view = new OleVariantView(getContext());
                view.setTag(i + 1);
                view.populateData(option);
                view.setVariantViewCallback(new OleVariantView.VariantViewCallback() {
                    @Override
                    public void selectItem(OleVariantView view, OleChoiceOption oleChoiceOption, String value) {
                        int index = findFromSelectedOptions(oleChoiceOption.getId());
                        if (index != -1) {
                            selectedOptions.get(index).setValue(value);
                        }
                        else {
                            OleProductVariant variant = new OleProductVariant(Integer.parseInt(view.getTag().toString()), oleChoiceOption.getId(), oleChoiceOption.getTitle(), value);
                            selectedOptions.add(variant);
                        }
                        setVariantPrice();
                    }
                });
                binding.variantVu.addView(view);
            }
        }
        else {
            binding.colorVu.setVisibility(View.GONE);
            currentStock = Integer.parseInt(productDetail.getCurrentStock());
            if (currentStock > 0) {
                binding.tvStock.setText(String.format(Locale.ENGLISH, "%d %s", currentStock, getString(R.string.in_stock)));
                binding.tvStock.setTextColor(getResources().getColor(R.color.greenColor));
            }
            else {
                binding.tvStock.setText(R.string.out_of_stock);
                binding.tvStock.setTextColor(getResources().getColor(R.color.redColor));
            }
        }

        if (productDetail.getFastDelivery().equalsIgnoreCase("1")) {
            binding.fastDeliveryVu.setVisibility(View.VISIBLE);
        }
        else {
            binding.fastDeliveryVu.setVisibility(View.GONE);
        }

        binding.tvCity.setText("");
        binding.tvDate.setText("");
        binding.tvOrderIn.setText("");
        for (OleDeliveryCity city: productDetail.getDeliveryData()) {
            if (city.getDefualt().equalsIgnoreCase("1")) {
                binding.tvCity.setText(city.getCityName());
                binding.tvDate.setText(city.getDeliveryDate());
                binding.tvOrderIn.setText(getString(R.string.order_in_place, city.getOrderIn()));
                break;
            }
        }

        binding.tvDesc.setText(productDetail.getDescription());
        reviewList.clear();
        reviewList.addAll(productDetail.getReviews());
        reviewsAdapter.notifyDataSetChanged();
        relatedProducts.clear();
        relatedProducts.addAll(productDetail.getRelated());
        productAdapter.notifyDataSetChanged();

        specsList.clear();
        specsList.addAll(productDetail.getSpecifications());
        specsAdapter.notifyDataSetChanged();

        if (productDetail.getDetailImg().equalsIgnoreCase("")) {
            binding.detailImgVu.setVisibility(View.GONE);
        }
        else {
            binding.detailImgVu.setVisibility(View.VISIBLE);
            Glide.with(this).load(productDetail.getDetailImg()).into(binding.detailImgVu);
        }

        if (productDetail.getFreeReturn().equalsIgnoreCase("1")) {
            binding.tvFreeReturn.setText(R.string.free_returns);
        }
        else {
            binding.tvFreeReturn.setText(R.string.non_returns);
        }

        if (productDetail.getLeaveAtDoor().equalsIgnoreCase("1")) {
            binding.contactlessDeliveryVu.setVisibility(View.VISIBLE);
        }
        else {
            binding.contactlessDeliveryVu.setVisibility(View.GONE);
        }
    }

    private int findFromSelectedOptions(String id) {
        int index = -1;
        for (int i = 0; i < selectedOptions.size(); i++) {
            if (selectedOptions.get(i).getId().equalsIgnoreCase(id)) {
                index = i;
                break;
            }
        }
        return index;
    }

    private void setVariantPrice() {
        int variantCount = productDetail.getChoiceOptions().size();
        if (productDetail.getColors() != null && !productDetail.getColors().isEmpty()) {
            variantCount = variantCount + 1;
        }
        if (selectedOptions.size() == variantCount) {
            Collections.sort(selectedOptions);
            String joinTitle = "";
            for (OleProductVariant variant : selectedOptions) {
                if (joinTitle.equalsIgnoreCase("")) {
                    joinTitle = variant.getValue();
                }
                else {
                    joinTitle = String.format("%s-%s", joinTitle, variant.getValue());
                }
            }
            OleAttributeCombination oleAttributeCombination = null;
            for (OleAttributeCombination combination : productDetail.getAttributeCombinations()) {
                if (combination.getTitle().equalsIgnoreCase(joinTitle)) {
                    oleAttributeCombination = combination;
                    break;
                }
            }
            if (oleAttributeCombination != null) {
                currentStock = Integer.parseInt(oleAttributeCombination.getQty());
                selectedCombinationId = oleAttributeCombination.getId();
                if (currentStock > 0) {
                    binding.tvStock.setText(String.format(Locale.ENGLISH, "%d %s", currentStock, getString(R.string.in_stock)));
                    binding.tvStock.setTextColor(getResources().getColor(R.color.greenColor));
                }
                else {
                    binding.tvStock.setText(R.string.out_of_stock);
                    binding.tvStock.setTextColor(getResources().getColor(R.color.redColor));
                }
                binding.tvPrice.setText(String.format("%s %s", oleAttributeCombination.getPrice(), productDetail.getCurrency()));
                setActualPrice(productDetail.getDiscount(), oleAttributeCombination.getPrice(), productDetail.getDiscountType(), productDetail.getCurrency());
            }
        }
    }

    private void setActualPrice(String discountValue, String salePrice, String discountType, String currency) {
        double discount = 0.0;
        if (!discountValue.equalsIgnoreCase("")) {
            discount = Double.parseDouble(discountValue);
        }
        if (discount > 0) {
            binding.discountVu.setVisibility(View.VISIBLE);
            double actualPrice = Double.parseDouble(salePrice);
            if (discountType.equalsIgnoreCase("amount")) {
                binding.tvDiscount.setText(String.format("-%s %s", discountValue, currency));
                double price = actualPrice - discount;
                binding.tvPrice.setText(String.format(Locale.ENGLISH,"%.2f %s", price, currency));
            }
            else {
                binding.tvDiscount.setText(String.format("-%s%%", discountValue));
                double price = actualPrice - ((discount / 100) * actualPrice);
                binding.tvPrice.setText(String.format(Locale.ENGLISH,"%.2f %s", price, currency));
            }
            binding.tvActualPrice.setText(salePrice);
            binding.tvActualPrice.setPaintFlags(binding.tvActualPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else {
            binding.tvActualPrice.setText("");
            binding.discountVu.setVisibility(View.GONE);
        }
    }

    private void setupSlider(List<String> imageDataList) {
        List<SlideModel> imageList = new ArrayList<>();
        for (String str: imageDataList) {
            imageList.add(new SlideModel(str,ScaleTypes.CENTER_CROP));
        }
        binding.slider.setImageList(imageList, ScaleTypes.FIT);
        binding.slider.setItemClickListener(new ItemClickListener() {
            @Override
            public void doubleClick(int i) {

            }

            @Override
            public void onItemSelected(int i) {
                new ImageViewer.Builder<>(getContext(), imageDataList).setFormatter(new ImageViewer.Formatter<String>() {
                    @Override
                    public String format(String s) {
                        return s;
                    }
                }).setStartPosition(i).show();
            }
        });
    }

//Use this code and change library version to 0.0.6
//    private void setupSlider(List<String> imageDataList) {
//        List<SlideModel> imageList = new ArrayList<>();
//        for (String str: imageDataList) {
//            imageList.add(new SlideModel(str));
//        }
//        binding.slider.setImageList(imageList, true);
//        binding.slider.setItemClickListener(new ItemClickListener() {
//            @Override
//            public void onItemSelected(int i) {
//                new ImageViewer.Builder<>(getContext(), imageDataList).setFormatter(new ImageViewer.Formatter<String>() {
//                    @Override
//                    public String format(String s) {
//                        return s;
//                    }
//                }).setStartPosition(i).show();
//            }
//        });
//    }

    private void getProductAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getProduct(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), productId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject obj = object.getJSONObject(Constants.kData);
                            Gson gson = new Gson();
                            productDetail = gson.fromJson(obj.toString(), Product.class);
                            populateData();
                        }
                        else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                }
                else {
                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                }
                else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }

    private void addCartAPI(boolean isLoader, String variants) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.addToCart(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), productId, selectedQty, selectedColor, selectedCombinationId, variants);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            currentStock = currentStock - selectedQty;
                            if (currentStock > 0) {
                                binding.tvStock.setText(String.format(Locale.ENGLISH, "%d %s", currentStock, getString(R.string.in_stock)));
                                binding.tvStock.setTextColor(getResources().getColor(R.color.greenColor));
                            }
                            else {
                                binding.tvStock.setText(R.string.out_of_stock);
                                binding.tvStock.setTextColor(getResources().getColor(R.color.redColor));
                            }
                            showCartDialog();
                        }
                        else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                }
                else {
                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                }
                else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }

    private void showCartDialog() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("AddedCartDialogFragment");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        OleAddedCartDialogFragment dialogFragment = new OleAddedCartDialogFragment();
        dialogFragment.setDialogCallback(new OleAddedCartDialogFragment.AddedCartDialogCallback() {
            @Override
            public void addedCart(DialogFragment df, boolean isViewCart) {
                df.dismiss();
                if (isViewCart) {
                    Intent intent = new Intent(getContext(), CartActivity.class);
                    startActivity(intent);
                }
            }
        });
        dialogFragment.show(fragmentTransaction, "AddedCartDialogFragment");
    }

    private void addToWishlist(boolean isLoader, String prodId, int pos) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        addToWishlistAPI(prodId, new BaseActivity.FavCallback() {
            @Override
            public void addRemoveFav(boolean success, String msg) {
                Functions.hideLoader(hud);
                if (success) {
                    if (pos == -1) {
                        productDetail.setIsFavorite("1");
                        binding.btnFav.setImageResource(R.drawable.shop_fav_ic);
                    }
                    else {
                        relatedProducts.get(pos).setIsFavorite("1");
                        productAdapter.notifyItemChanged(pos);
                    }
                }
                else {
                    Functions.showToast(getContext(), msg, FancyToast.ERROR);
                }
            }
        });
    }

    private void removeFromWishlist(boolean isLoader, String prodId, int pos) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        removeFromWishlistAPI(prodId, new BaseActivity.FavCallback() {
            @Override
            public void addRemoveFav(boolean success, String msg) {
                Functions.hideLoader(hud);
                if (success) {
                    if (pos == -1) {
                        productDetail.setIsFavorite("0");
                        binding.btnFav.setImageResource(R.drawable.shop_unfav_ic);
                    }
                    else {
                        relatedProducts.get(pos).setIsFavorite("0");
                        productAdapter.notifyItemChanged(pos);
                    }
                }
                else {
                    Functions.showToast(getContext(), msg, FancyToast.ERROR);
                }
            }
        });
    }

}