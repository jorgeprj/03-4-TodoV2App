package com.example.todov1app;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.todov1app.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    ArrayList<Task> tasks = new ArrayList<>();
    TaskRecyclerViewAdapter taskRecyclerViewAdapter;
    int currentPosition;
    ActionMode currentActionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Log.d("MainActivity", "onCreate");

        // Se tiver uma instancia salva
        if (savedInstanceState != null) {
            tasks = (ArrayList<Task>) savedInstanceState.getSerializable("tasks");
            if (tasks == null) tasks = new ArrayList<>();
        }

        binding.addButton.setOnClickListener(view -> {
            // Desabilita o action mode quando clica no botão Add
            if (currentActionMode != null)
                currentActionMode.finish();

            Intent i = new Intent(MainActivity.this, AddTaskActivity.class);
            addTaskResultLauncher.launch(i);
        });

        binding.taskListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.taskListRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        taskRecyclerViewAdapter = new TaskRecyclerViewAdapter(MainActivity.this, tasks);
        binding.taskListRecyclerView.setAdapter(taskRecyclerViewAdapter);

        taskRecyclerViewAdapter.setLongClickListener((view, position) -> {
            if (currentActionMode != null)
                return;

            currentPosition = position;  // Armazena o item atualmente selecionado
            currentActionMode = startActionMode(modeCallBack);
            view.setSelected(true);
        });
    }

    ActivityResultLauncher<Intent> addTaskResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Task tTask = (Task) data.getSerializableExtra("taskAdded");
                        Log.d("MainActivity", tTask.toString());
                        // Update RecyclerView
                        tasks.add(tTask);
                        taskRecyclerViewAdapter.notifyDataSetChanged();
                    }
                }
            }
    );

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.d("MainActivity", "onSaveInstanceState()");
        savedInstanceState.putSerializable("tasks", tasks);
    }

    private ActionMode.Callback modeCallBack = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.setTitle("Actions");
            mode.getMenuInflater().inflate(R.menu.context_menu, menu);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.showItem) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                Task tTask = tasks.get(currentPosition);
                String tMsg = "Name: " + tTask.getName()
                            + "\nDescription: " + tTask.getDescription()
                            + "\nPriority: " + tTask.getPriority();
                builder.setTitle("Task details");
                builder.setMessage(tMsg);
                builder.setPositiveButton("OK", null);
                builder.create().show();
                mode.finish();    // Encerra o action mode
                return true;
            } else if (item.getItemId() == R.id.deleteItem) {
                tasks.remove(currentPosition);
                taskRecyclerViewAdapter.notifyDataSetChanged();
                mode.finish();    // Encerra o action mode
                return true;
            } else if (item.getItemId() == R.id.toTopItem) {
                Task tTask = tasks.remove(currentPosition);
                tasks.add(0, tTask); // Adiciona no início da lista
                taskRecyclerViewAdapter.notifyDataSetChanged();
                mode.finish();    // Encerra o action mode
                return true;
            } else if (item.getItemId() == R.id.toEndItem) {
                Task tTask = tasks.remove(currentPosition);
                tasks.add(tTask); // Adiciona no final da lista
                taskRecyclerViewAdapter.notifyDataSetChanged();
                mode.finish();    // Encerra o action mode
                return true;
            } else if (item.getItemId() == R.id.prioritizeItem) {
                Task tTask = tasks.get(currentPosition);
                String currentPriority = tTask.getPriority();
                if (currentPriority.equals("low")) {
                    tTask.setPriority("medium");
                } else if (currentPriority.equals("medium")) {
                    tTask.setPriority("high");
                } else if (currentPriority.equals("high")) {
                    Toast.makeText(MainActivity.this, "Task already has HIGH priority!", Toast.LENGTH_SHORT).show();
                }
                taskRecyclerViewAdapter.notifyDataSetChanged();
                mode.finish();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            currentActionMode = null; // Limpa o modo atual
        }
    };
}