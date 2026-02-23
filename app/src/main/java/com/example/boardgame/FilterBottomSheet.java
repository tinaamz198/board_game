package com.example.boardgame;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class FilterBottomSheet extends BottomSheetDialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_filter_bottom_sheet, container, false); // убедись, что имя XML совпадает

        ChipGroup cgCategories = view.findViewById(R.id.cgCategories);
        ChipGroup cgDifficulty = view.findViewById(R.id.cgDifficulty);
        Button btnApply = view.findViewById(R.id.btnApplyFilter);

        btnApply.setOnClickListener(v -> {
            // 1. Получаем текст выбранной категории
            String selectedCategory = "";
            int catId = cgCategories.getCheckedChipId();
            if (catId != View.NO_ID) {
                selectedCategory = ((Chip) cgCategories.findViewById(catId)).getText().toString();
            }

            // 2. Получаем текст сложности
            String selectedDiff = "";
            int diffId = cgDifficulty.getCheckedChipId();
            if (diffId != View.NO_ID) {
                selectedDiff = ((Chip) cgDifficulty.findViewById(diffId)).getText().toString();
            }

            // 3. Отправляем данные обратно во фрагмент через Bundle
            Bundle result = new Bundle();
            result.putString("category", selectedCategory);
            result.putString("difficulty", selectedDiff);

            // Ключ "filter_request" должен совпадать с тем, что мы напишем во фрагменте
            getParentFragmentManager().setFragmentResult("filter_request", result);

            dismiss(); // Закрываем шторку
        });

        return view;
    }
}