package com.example.boardgame.mafia;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.boardgame.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MafiaSetupFragment extends Fragment {

    private LinearLayout rolesContainer;
    private final Map<String, Integer> selectedRoles = new HashMap<>();
    private final String[] roleNames = {"Ведущий", "Мафия", "Мирный житель", "Дон мафии", "Доктор", "Комиссар", "Маньяк"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mafia_setup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rolesContainer = view.findViewById(R.id.rolesContainer);
        Button btnContinue = view.findViewById(R.id.btnContinue);

        // Динамически создаем список ролей
        for (String role : roleNames) {
            addRoleRow(role);
        }

        btnContinue.setOnClickListener(v -> {
            ArrayList<String> rolesList = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : selectedRoles.entrySet()) {
                for (int i = 0; i < entry.getValue(); i++) {
                    rolesList.add(entry.getKey());
                }
            }

            if (rolesList.isEmpty()) {
                Toast.makeText(getContext(), "Выберите хотя бы одну роль!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Передаем список ролей в саму игру
            Bundle b = new Bundle();
            b.putStringArray("rolesList", rolesList.toArray(new String[0]));
            Navigation.findNavController(v).navigate(R.id.action_mafiaSetupFragment_to_mafiaGameFragment, b);
        });

        view.findViewById(R.id.btnBack).setOnClickListener(v -> Navigation.findNavController(v).popBackStack());
    }

    private void addRoleRow(String name) {
        View row = getLayoutInflater().inflate(R.layout.item_mafia_role_setup, rolesContainer, false);
        TextView tvName = row.findViewById(R.id.roleName);
        TextView tvCount = row.findViewById(R.id.roleCount);
        View btnPlus = row.findViewById(R.id.btnPlus);
        View btnMinus = row.findViewById(R.id.btnMinus);

        tvName.setText(name);
        selectedRoles.put(name, 0);

        btnPlus.setOnClickListener(v -> {
            int count = selectedRoles.get(name) + 1;
            selectedRoles.put(name, count);
            tvCount.setText(String.valueOf(count));
        });

        btnMinus.setOnClickListener(v -> {
            int count = selectedRoles.get(name);
            if (count > 0) {
                count--;
                selectedRoles.put(name, count);
                tvCount.setText(String.valueOf(count));
            }
        });

        rolesContainer.addView(row);
    }
}