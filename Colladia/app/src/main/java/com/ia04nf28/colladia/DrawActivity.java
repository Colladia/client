package com.ia04nf28.colladia;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.ia04nf28.colladia.model.Diagram;
import com.ia04nf28.colladia.model.Elements.Element;
import com.ia04nf28.colladia.model.Elements.ElementFactory;
import com.ia04nf28.colladia.model.Manager;
import com.szugyi.circlemenu.view.CircleLayout;

public class DrawActivity extends AppCompatActivity implements Manager.CurrentDiagramListener {

    private static final String TAG = "DrawActivity";

    private DrawerLayout drawer;
    ActionBarDrawerToggle drawerToggle;
    private NavigationView nav;
    private DrawColladiaView colladiaView;

    private CircleLayout mainContextualMenu;
    private CircleLayout selectContextualMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        // Register to currentDiagram listener
        Manager.instance(getApplicationContext()).registerListener(this);
        Manager.instance(getApplicationContext()).synchronizeDiagramState();

        // Change toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(Manager.instance(getApplicationContext()).getCurrentDiagram().getName());

        if(getSupportActionBar().getTitle() == null)
        {
            getSupportActionBar().setTitle("Colladia");
        }


        colladiaView = (DrawColladiaView) findViewById(R.id.draw_view);
        colladiaView.setApplicationCtx(getApplicationContext());

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        // Add burger button
        drawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(drawerToggle);

        // Removes overlay
        drawer.setScrimColor(Color.TRANSPARENT);
        drawer.closeDrawers();

        mainContextualMenu = (CircleLayout) findViewById(R.id.main_contextual_menu);
        selectContextualMenu = (CircleLayout) findViewById(R.id.select_contextual_menu);
        colladiaView.setMainContextualMenu(mainContextualMenu);
        colladiaView.setSelectContextualMenu(selectContextualMenu);

        nav = (NavigationView) findViewById(R.id.nav_view);

        nav.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        selectDrawerItem(item);
                        return true;
                    }
                }
        );
    }

    public void selectDrawerItem(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.nav_home:
                finish();
                break;

            case R.id.nav_position:
                Manager.instance(getApplicationContext()).autoPositioning();
                colladiaView.setMode(0);
                drawer.closeDrawers();
                break;

            case R.id.nav_center:
                colladiaView.recenter();
                drawer.closeDrawers();
                break;

            default:
                Element newElement = ElementFactory.createElement(getApplicationContext(), item.getTitle().toString());
                if (newElement != null) colladiaView.insertNewElement(newElement);
                drawer.closeDrawers();
                break;
        }
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // This will toggle the drawer if android.R.id.home is clicked
        int id = item.getItemId();
        switch(id)
        {
            case R.id.nav_home:
                drawer.openDrawer(GravityCompat.START);
                return true;

            default:
                return true;
        }
    }



    @Override
    public void onStateChange(Diagram currentDiagram) {
        //Toast.makeText(getApplicationContext(), "diagram name changed", Toast.LENGTH_LONG).show();
        try{
            getSupportActionBar().setTitle(Manager.instance(getApplicationContext()).getCurrentDiagram().getName());
        }catch (Throwable e) {}
    }



    @Override
    protected void onResume() {
        super.onResume();
        Manager.instance(getApplicationContext()).joinWorkspace();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Manager.instance(getApplicationContext()).quitWorkspace();
    }
}
