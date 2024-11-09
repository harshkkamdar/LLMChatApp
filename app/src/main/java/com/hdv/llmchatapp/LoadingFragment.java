package com.hdv.llmchatapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LoadingFragment extends Fragment {
    private Callback loadingCallback;
    private TextView errorText;
    private ProgressBar progressBar;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loading, container, false);
        errorText = view.findViewById(R.id.error_text);
        progressBar = view.findViewById(R.id.progress_bar);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadModel();
    }

    private void loadModel() {
        executor.execute(() -> {
            try {
                InferenceModel.getInstance(requireContext().getApplicationContext());
                requireActivity().runOnUiThread(() -> {
                    if (loadingCallback != null) {
                        loadingCallback.onModelLoaded();
                    }
                });
            } catch (Exception e) {
                String errorMessage = e.getLocalizedMessage();
                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    errorText.setText(errorMessage);
                    errorText.setVisibility(View.VISIBLE);
                });
            }
        });
    }

    public void setLoadingCallback(Callback callback) {
        this.loadingCallback = callback;
    }

    interface Callback {
        void onModelLoaded();
    }
} 