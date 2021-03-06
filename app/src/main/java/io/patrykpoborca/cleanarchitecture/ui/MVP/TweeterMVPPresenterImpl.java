package io.patrykpoborca.cleanarchitecture.ui.MVP;

import io.patrykpoborca.cleanarchitecture.network.TweeterApi;
import io.patrykpoborca.cleanarchitecture.network.base.Retrofit;
import io.patrykpoborca.cleanarchitecture.ui.MVP.interfaces.TweeterMVPPView;
import io.patrykpoborca.cleanarchitecture.ui.MVP.interfaces.TweeterMVPPresenter;

/**
 * Created by Patryk on 7/28/2015.
 */
public class TweeterMVPPresenterImpl implements TweeterMVPPresenter {

    private static final int TWEET_COUNT = 2;
    private final TweeterApi tweeterApi;
    private final Retrofit retrofit;
    private TweeterMVPPView mainMVPView;
    private int tweetsAdded = 0;


    public TweeterMVPPresenterImpl(TweeterApi tweeterApi, Retrofit retrofit) {
        this.tweeterApi = tweeterApi;
        this.retrofit = retrofit;
    }

    @Override
    public void registerView(TweeterMVPPView activity) {
        this.mainMVPView = activity;
    }

    @Override
    public void onAttach() {

    }

    @Override
    public void onDetach() {

    }

    @Override
    public void fetchCurrentTweet() {
        mainMVPView.toggleProgressBar(true);
        tweeterApi.getTweet().subscribe(s -> {
            mainMVPView.toggleProgressBar(false);
            tweetsAdded ++;
            if(tweetsAdded > TWEET_COUNT){
                mainMVPView.displayToast("Tweet size exceeded " + TWEET_COUNT);
            }
            this.mainMVPView.displayFetchedTweet(s);
        });
    }

    @Override
    public void fetchPreviousTweets() {

        mainMVPView.toggleProgressBar(true);
        tweeterApi.fetchXrecents(TWEET_COUNT)
                .subscribe(l -> {
                    mainMVPView.displayPreviousTweets(l);
                    mainMVPView.toggleProgressBar(false);
                });
    }

    @Override
    public void toggleLogin(String userName, String userPassword) {
        mainMVPView.toggleProgressBar(true);
        if(tweeterApi.isLoggedIn()){
            tweeterApi.logout()
                    .subscribe(s -> {
                        mainMVPView.toggleProgressBar(false);
                        mainMVPView.setUserButtonText("Login");
                        mainMVPView.displayToast("User logged out");
                        mainMVPView.toggleLoginContainer(true);
                        //could implement more literal less reusable methods, such as loggedIn and loggedOut such as in the MVPCI example.
                        //However I wanted to be extremely verbose in the MVP example.
                    });
        }
        else {
            this.tweeterApi.login(userName, userPassword)
                    .subscribe(userProfile -> {
                        mainMVPView.displayToast(userProfile.getFormattedCredentials() + " Logged in");
                        mainMVPView.setUserButtonText("Log " + userProfile.getUserName() + " out");
                        mainMVPView.toggleProgressBar(false);
                        mainMVPView.toggleLoginContainer(false);
                    });
        }
    }

    @Override
    public void loadWebPage(String url) {
        mainMVPView.toggleProgressBar(true);
        retrofit.fetchSomePage(url)
                .subscribe(s -> {
                    mainMVPView.displayWebpage(s);
                    mainMVPView.toggleProgressBar(false);
                });
    }
}
