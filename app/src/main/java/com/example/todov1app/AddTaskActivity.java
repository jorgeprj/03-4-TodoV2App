package com.example.todov1app;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;

import com.example.todov1app.databinding.ActivityAddTaskBinding;

public class AddTaskActivity extends AppCompatActivity {

    ActivityAddTaskBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.addNewButton.setOnClickListener(view -> {
            String name = binding.taskNameEditText.getText().toString();
            String desc = binding.taskDescEditText.getText().toString();

            String priority = "low"; // default

            int selectedId = binding.priorityRadioGroup.getCheckedRadioButtonId();
            if (selectedId != -1) {
                RadioButton selected = findViewById(selectedId);
                priority = selected.getText().toString().toLowerCase(); // "high", "medium", "low"
            }

            Task task = new Task(name, desc, priority);
            Intent i = new Intent();
            i.putExtra("taskAdded", task);
            setResult(RESULT_OK, i);
            finish();
        });
    }
}