package com.android.NBZxing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ailiwean.core.Result;
import com.ailiwean.core.view.style1.NBZxingView;

import org.jetbrains.annotations.NotNull;

/**
 * @Package: com.android.NBZxing
 * @ClassName: ZxingFragment
 * @Description:
 * @Author: SWY
 * @CreateDate: 2020/7/21 4:32 PM
 */
public class ZxingFragment extends Fragment {

    private NBZxingView NBZxingView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        NBZxingView = new NBZxingView(container.getContext()) {
            @Override
            public void resultBack(@NotNull Result content) {
                Toast.makeText(getContext(), content.getText(), Toast.LENGTH_LONG).show();
            }
        };
        return NBZxingView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NBZxingView.synchLifeStart(this);
    }
}
