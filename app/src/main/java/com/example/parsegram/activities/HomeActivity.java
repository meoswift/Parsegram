package com.example.parsegram.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.parsegram.R;
import com.example.parsegram.fragments.ComposeFragment;
import com.example.parsegram.fragments.PostsFragment;
import com.example.parsegram.fragments.ProfileFragment;
import com.example.parsegram.models.Post;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

import org.parceler.Parcels;

// Activity that allows user to navigate between 3 tabs: Home, Create post, and User profile.
public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Find views and define fragments
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        // Define a fragment manager for bottom navigation
        final FragmentManager fragmentManager = getSupportFragmentManager();

        // Navigate user to the correct tab when they choose a menu item
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment = null;
                // Switch fragments depending on chosen menu item
                switch (menuItem.getItemId()) {
                    case R.id.action_home:
                        fragment = new PostsFragment();
                        break;
                    case R.id.action_create:
                        fragment = new ComposeFragment();
                        break;
                    case R.id.action_profile:
                        fragment = new ProfileFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString(Post.KEY_USERNAME, ParseUser.getCurrentUser().getUsername());
                        fragment.setArguments(bundle);
                        break;
                    default:
                        break;
                }
                // Replace the contents of the container with the new fragment and update in view
                fragmentManager.beginTransaction().replace(R.id.your_placeholder, fragment).commit();
                return true;
            }
        });
        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }
}