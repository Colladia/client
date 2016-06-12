package com.ia04nf28.colladia;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.content.DialogInterface;
import android.databinding.ObservableList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.lang.String;
import java.util.Collections;
import java.util.List;
import com.ia04nf28.colladia.model.Manager;

public class WorkspacesListActivity extends AppCompatActivity {

    private EditText userTextInput;
    private String diagramSelected;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create a progress bar to display while the list loads
//        ProgressBar progressBar = new ProgressBar(this);
//        progressBar.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT,
//                AbsListView.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
//        progressBar.setIndeterminate(true);
//        getListView().setEmptyView(progressBar);

        // Must add the progress bar to the root of the layout
//        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
//        root.addView(progressBar);

        setContentView(R.layout.activity_workspaces_list);

        listView = (ListView) findViewById(R.id.diagrams_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onListItemClick(listView, view, position, id);
            }
        });

        updateAdapter(Manager.instance(getApplicationContext()).getDiagrams());
        Manager.instance(getApplicationContext()).addOnDiagramsChangeCallback(new ObservableList.OnListChangedCallback<ObservableList<String>>() {
            @Override
            public void onChanged(ObservableList<String> sender) {
                updateAdapter(sender);
            }

            @Override
            public void onItemRangeChanged(ObservableList<String> sender, int positionStart, int itemCount) {
                updateAdapter(sender);
                System.out.println("onItemRangeChanged");
            }

            @Override
            public void onItemRangeInserted(ObservableList<String> sender, int positionStart, int itemCount) {
                updateAdapter(sender);
                System.out.println("onItemRangeInserted");
            }


            @Override
            public void onItemRangeMoved(ObservableList<String> sender, int fromPosition, int toPosition, int itemCount) {
                updateAdapter(sender);
                System.out.println("onItemRangeMoved");
            }

            @Override
            public void onItemRangeRemoved(ObservableList<String> sender, int positionStart, int itemCount) {
                updateAdapter(sender);
                System.out.println("onItemRangeRemoved");
            }

        });


        FloatingActionButton addBut = (FloatingActionButton) findViewById(R.id.addButton);
        addBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(WorkspacesListActivity.this);
                userTextInput = new EditText(WorkspacesListActivity.this);

                builder.setTitle(getString(R.string.add_diagram_title))
                        .setView(userTextInput)
                        .setPositiveButton(R.string.add_diagram_button, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface di, int i) {
                                String diagramName = userTextInput.getText().toString().trim().replaceAll(" ", "_");
                                if (!diagramName.isEmpty())
                                    Manager.instance(getApplicationContext()).addDiagram(diagramName);
                            }
                        })
                        .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface di, int i) {

                            }
                        })
                        .create()
                        .show();
            }
        });

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.title_activity_worspaces_list);
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        // Do something when a list item is clicked

        Object o = l.getItemAtPosition(position);
        diagramSelected = o.toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(WorkspacesListActivity.this);

        // Neutral button is on center
        builder.setPositiveButton(R.string.access_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface di, int i) {
                Manager.instance(getApplicationContext()).setCurrentDiagram(diagramSelected);
                Intent intent = new Intent(WorkspacesListActivity.this, DrawActivity.class);
                startActivity(intent);
            }
        });
        // Negative button is on the left
        builder.setNegativeButton(R.string.delete_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface di, int i) {
                AlertDialog.Builder builder = new AlertDialog.Builder(WorkspacesListActivity.this);

                builder.setTitle(getString(R.string.delete_title) + " " + diagramSelected + " ?");

                builder.setPositiveButton(R.string.delete_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface di, int i) {
                        Manager.instance(getApplicationContext()).removeDiagram(diagramSelected);
                    }
                });

                builder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface di, int i) {

                    }
                });

                builder.create().show();
            }
        });

        // Positive button is on the right
        builder.setNeutralButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface di, int i) {

            }
        });

        builder.create().show();
    }

    private void updateAdapter(List<String> list){
        listView.setAdapter(new ArrayAdapter<String>(this,R.layout.list_workspaces,list));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Manager.instance(getApplicationContext()).quitServer();
    }


}
