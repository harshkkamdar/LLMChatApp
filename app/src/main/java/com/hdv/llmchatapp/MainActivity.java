package com.hdv.llmchatapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.hdv.llmchatapp.ui.ChatListFragment;

public class MainActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        if (savedInstanceState == null) {
            showLoadingFragment();
        }
    }
    
    private void showLoadingFragment() {
        LoadingFragment fragment = new LoadingFragment();
        fragment.setLoadingCallback(this::showChatListFragment);
        replaceFragment(fragment);
    }
    
    private void showChatListFragment() {
        replaceFragment(new ChatListFragment());
    }
    
    public void showChatFragment(long chatId) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, ChatFragment.newInstance(chatId));
        transaction.addToBackStack(null);
        transaction.commit();
    }
    
    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }
} 