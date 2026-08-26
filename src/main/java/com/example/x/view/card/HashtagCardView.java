package com.example.x.view.card;

import com.example.x.Main;
import com.example.x.model.hashtag.Hashtag;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.io.IOException;


public class HashtagCardView {

    @FXML private Label hashtagTitleLabel;
    @FXML private Label usageCountLabel;

    private Hashtag hashtag;

    public void setHashtagData(Hashtag hashtag, Runnable onClick) {
        this.hashtag = hashtag;
        hashtagTitleLabel.setText(hashtag.getTitle());
        usageCountLabel.setText(hashtag.getUsageCount() + " posts");
    }

    @FXML
    void hashtagClicked(MouseEvent event) throws IOException {
        if (hashtag != null) {
            Main.getInstance().goToHashtagPage(hashtag);
        }
    }
}