package ae.oleapp.dialogs;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import com.shashank.sony.fancytoastlib.FancyToast;

import ae.oleapp.R;
import ae.oleapp.databinding.OlefragmentPaymentWebDialogBinding;
import ae.oleapp.util.Functions;

/**
 * A simple {@link Fragment} subclass.
 */
//@SuppressLint("SetJavaScriptEnabled")
public class PaymentWebDialogFragment extends DialogFragment {

    private OlefragmentPaymentWebDialogBinding binding;
    private String urlStr = "";
    private PaymentWebCallback paymentCallback;
    private String result = null;

    public PaymentWebDialogFragment() {
        // Required empty public constructor
    }

    public PaymentWebDialogFragment(String urlStr) {
        this.urlStr = urlStr;
    }

    public void setPaymentCallback(PaymentWebCallback paymentCallback) {
        this.paymentCallback = paymentCallback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentPaymentWebDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        binding.titleBar.toolbarTitle.setText(getResources().getString(R.string.payment));
        binding.titleBar.backBtn.setVisibility(View.GONE);

        binding.webVu.getSettings().setJavaScriptEnabled(true);
        binding.webVu.setWebViewClient(webViewClient);
        binding.webVu.loadUrl(urlStr);

        binding.btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doneClicked();
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    WebViewClient webViewClient = new WebViewClient() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            result = checkResult(url);
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            result = checkResult(url);
            return super.shouldOverrideUrlLoading(view, url);
        }
    };

    private String checkResult(String url) {
        if (url.contains("malaebole.com/payment_status/success")) {
            return "1";
        }
        else if (url.contains("malaebole.com/payment_status/failed")) {
            return "0";
        }
        else {
            return null;
        }
    }

    private void doneClicked() {
        if (result != null) {
            paymentCallback.didPaymentDone(result);
            dismiss();
        }
        else {
            Functions.showToast(getContext(), getResources().getString(R.string.payment_is_in_process), FancyToast.ERROR);
        }
    }

    public interface PaymentWebCallback {
        void didPaymentDone(String result);
    }
}
