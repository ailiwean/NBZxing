package com.android.NBZxing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ailiwean.core.view.ZxingCameraView;

import org.jetbrains.annotations.NotNull;

/**
 * @Package: com.android.NBZxing
 * @ClassName: ZxingFragment
 * @Description:
 * @Author: SWY
 * @CreateDate: 2020/7/21 4:32 PM
 */
public class ZxingFragment extends Fragment {

    private ZxingCameraView zxingCameraView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        zxingCameraView = new ZxingCameraView(container.getContext()) {
            @Override
            public void resultBack(@NotNull String content) {
                Toast.makeText(container.getContext(), content, Toast.LENGTH_LONG).show();
            }
        };
        return zxingCameraView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        zxingCameraView.synchLifeStart(this);
    }
}
