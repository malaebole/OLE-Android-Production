package ae.oleapp.activities;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityCreateGiftBinding;
import ae.oleapp.databinding.ActivityOleFinanceBinding;
import ae.oleapp.databinding.ActivityPdfViewerBinding;
import ae.oleapp.databinding.FragmentDocDetailsBottomSheetDialogBinding;
import ae.oleapp.util.Functions;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.net.ssl.HttpsURLConnection;

public class PdfViewerActivity extends BaseActivity implements View.OnClickListener{
    private ActivityPdfViewerBinding binding;
    private String pdfUrl = "", folderName= "", fileName= "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfViewerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        pdfUrl = getIntent().getStringExtra("file_url");
        folderName = getIntent().getStringExtra("folder_name");
        fileName = getIntent().getStringExtra("file_name");

        RetrievePDFFromURL retrievePDFFromURL = new RetrievePDFFromURL(binding.pdf);
        retrievePDFFromURL.execute(pdfUrl);
        binding.btnDownload.setOnClickListener(this);
    }


    private void downloadPdf(String fileUrl) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, folderName+"_"+fileName+".pdf");
        DownloadManager downloadManager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
        Functions.showToast(getContext(),"File is Downloading. . .", FancyToast.SUCCESS);
        this.finish();
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnDownload){
            downloadPdf(pdfUrl);
        }
    }

    private static class RetrievePDFFromURL extends AsyncTask<String, Void, InputStream> {

        private final PDFView pdfView;

        public RetrievePDFFromURL(PDFView pdfView) {
            this.pdfView = pdfView;
        }

        @Override
        protected InputStream doInBackground(String... params) {
            InputStream inputStream = null;
            try {
                URL url = new URL(params[0]);
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

                if (urlConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream result) {
            if (result != null) {
                pdfView.fromStream(result).load();
            } else {
                Toast.makeText(pdfView.getContext(), "Failed to load PDF", Toast.LENGTH_SHORT).show();
            }
        }
    }
}