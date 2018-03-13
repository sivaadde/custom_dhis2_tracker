/*
* This fragment displays details about individual videos, resources etc..
*/

package edu.iupui.soic.biohealth.plhi.mhbs.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;

import java.io.File;

import edu.iupui.soic.biohealth.plhi.mhbs.R;
import edu.iupui.soic.biohealth.plhi.mhbs.documents.ResourceItemDownloader;

public class PdfDetailsFragment extends Fragment implements ResourceItemDownloader.DownloadResponse {
    private String itemToDownload = "";
    private File openResource;
    private View pView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (checkRunTimePermissions()) {
            itemToDownload = getArguments().getString("resourceId");
            String itemType = getArguments().getString("resourceKey");
            ResourceItemDownloader myDownloader = new ResourceItemDownloader(getContext(), itemToDownload, itemType, this);
            myDownloader.setupFiles();
            myDownloader.tryToDownload();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        // Inflate the xml file for the fragment
        View rootView = inflater.inflate(R.layout.fragment_pdf_details, parent, false);
        pView = rootView;
        return rootView;
    }

    private boolean checkRunTimePermissions() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else {
            return true;
        }
        return false;
    }


    @Override
    public void onDownloadFinish(String fileName) {
        // the resource lies in internal storage
        if (fileName.contains("app_mhbsDocs")) {
            File parentDir = getContext().getExternalFilesDir(null);
            // always points to internal memory (note, automatically concatenates app_ by default)
            File dir = getContext().getDir("mhbsDocs", Context.MODE_PRIVATE);
            File internalFile = new File(parentDir +"/" + dir);
            openResource =  new File(internalFile + "/" + itemToDownload + ".pdf");
        } else {
            // resource is in external storage
            openResource = new File(Environment.getExternalStorageDirectory().getPath() + fileName + "/" + itemToDownload + ".pdf");
        }
        PDFView pdfView = (PDFView) pView.findViewById(R.id.pdfView);
        if (openResource != null) {
            pdfView.fromFile(openResource).enableDoubletap(true).enableSwipe(true).scrollHandle(new DefaultScrollHandle(getContext())).swipeHorizontal(false).defaultPage(0).load();
        }
    }
}