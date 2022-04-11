package com.example.bikerx.ui.chat;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.bikerx.R;

import java.util.ArrayList;


/**
 * A fragment class that inherits the built-in Fragment class to support user interaction relating to ForumThread objects.
 * This class mainly offers functionalities for the Chat/Forum component of the application.
 *
 * @author Xuan Hua
 * @version 1.0, 25/03/2022
 * @since 17.0.2
 *
 */
public class ChatFragment extends Fragment {
    /**
     * The view model of this ChatFragment object.
     * @see ChatViewModel
     */
    private ChatViewModel cViewModel;
    /**
     * The recycler view of this ChatFragment object.
     * @see RecyclerView
     */
    private RecyclerView cRecyclerView;
    /**
     * The inflated view of this ForumThread object.
     * @see View
     */
    private View view;

    /**
     * Starts a new instance of the ChatFragment class
     * @return a new instance of ChatFragment class
     */
    public static ChatFragment newInstance() {
        return new ChatFragment();
    }

    /**
     * Overrides the original onCreateView to assign the view model and view
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        cViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        view = inflater.inflate(R.layout.chat_fragment, container, false);
        return view;
    }

    /**
     * Overrides the original onViewCreated to set up the forum thread recycler view
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        displayForumThread(view);
    }

    /**
     * Calls the fetchForumThread and getForumThread method from the view model, and set up a recycler view for ForumThread objects.
     * Displays an error message if there is no data fetched (TO-DO)
     * @param view
     */
    private void displayForumThread(View view) {
        cViewModel.fetchForumThread();
//        if (cViewModel.getForumThread().getValue().isEmpty()) {
//            Toast.makeText(getActivity().getApplicationContext(), "Error Getting Data", Toast.LENGTH_LONG).show();
//        }
        cViewModel.getForumThread().observe(this, new Observer<ArrayList<ForumThread>>() {
            @Override
            public void onChanged(ArrayList<ForumThread> forumThreadArray) {
                cRecyclerView = view.findViewById(R.id.forumThreadRecycleView);
                cRecyclerView.setHasFixedSize(true);
                LinearLayoutManager cLayoutManager = new LinearLayoutManager(view.getContext());
                cRecyclerView.setLayoutManager(cLayoutManager);
                cRecyclerView.setAdapter(new ForumThreadAdapter(forumThreadArray, communication));
            }
        });
    }

    /**
     * Implements a public interface to connect the onClick method between the ChatFragment and ForumThreadAdapter
     * Navigates from the current ChatFragment to the corresponding MessageFragment, with the threadId and threadName arguments
     * @see FragmentCommunication
     * @see ChatFragment
     * @see ForumThreadAdapter
     */
    FragmentCommunication communication = new FragmentCommunication() {
        @Override
        public void onForumClick(String threadId, String threadName) {
            NavDirections action = ChatFragmentDirections.actionNavigationChatToStartMessageFragment(threadId, threadName);
            Navigation.findNavController(view).navigate(action);
        }
    };

    /**
     * Overrides the original onDestroyView
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
