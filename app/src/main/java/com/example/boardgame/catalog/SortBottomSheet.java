package com.example.boardgame.catalog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.boardgame.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class SortBottomSheet extends BottomSheetDialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_sort_bottom_sheet, container, false);

        v.findViewById(R.id.btnSortAsc).setOnClickListener(view -> sendResult(true));
        v.findViewById(R.id.btnSortDesc).setOnClickListener(view -> sendResult(false));

        return v;
    }

    private void sendResult(boolean isAsc) {
        Bundle result = new Bundle();
        result.putBoolean("isAscending", isAsc);
        getParentFragmentManager().setFragmentResult("sort_request", result);
        dismiss();
    }
}