package org.xersys.kumander.util;

import com.sun.javafx.scene.control.skin.BehaviorSkinBase;
import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class FXUtil {
    private final static String pxeModuleName = FXUtil.class.getSimpleName();
    
    public static void showModal(Application foObj) throws Exception{
        Stage stage = new Stage();
        stage.initModality(Modality.NONE);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setAlwaysOnTop(true);
        foObj.start(stage);     
    }

    public static void SetNextFocus(TextField foField){
        if( foField.getSkin() instanceof BehaviorSkinBase) {
            ((BehaviorSkinBase) foField.getSkin()).getBehavior().traverseNext();  
        }
    }
    public static void SetNextFocus(TextArea foField){
        if( foField.getSkin() instanceof BehaviorSkinBase) {
            ((BehaviorSkinBase) foField.getSkin()).getBehavior().traverseNext();  
        }
    }
    public static void SetNextFocus(ComboBox foField){
        if( foField.getSkin() instanceof BehaviorSkinBase) {
            ((BehaviorSkinBase) foField.getSkin()).getBehavior().traverseNext();  
        }
    }    
    
    public static void SetNextFocus(TabPane foField){
        if( foField.getSkin() instanceof BehaviorSkinBase) {
            ((BehaviorSkinBase) foField.getSkin()).getBehavior().traverseNext();  
        }
    }
  
    public static void SetNextFocus(CheckBox foField){
        if( foField.getSkin() instanceof BehaviorSkinBase) {
            ((BehaviorSkinBase) foField.getSkin()).getBehavior().traverseNext();  
        }
    }
    
    public static void SetPreviousFocus(TextField foField){
        if( foField.getSkin() instanceof BehaviorSkinBase) {
            ((BehaviorSkinBase) foField.getSkin()).getBehavior().traversePrevious();
        }
    }
    
    public static void SetPreviousFocus(TabPane foField){
        if( foField.getSkin() instanceof BehaviorSkinBase) {
            ((BehaviorSkinBase) foField.getSkin()).getBehavior().traversePrevious();
        }
    }
    
    public static void SetPreviousFocus(TextArea foField){
        if( foField.getSkin() instanceof BehaviorSkinBase) {
            ((BehaviorSkinBase) foField.getSkin()).getBehavior().traversePrevious();  
        }
    }
    public static void SetPreviousFocus(ComboBox foField){
        if( foField.getSkin() instanceof BehaviorSkinBase) {
            ((BehaviorSkinBase) foField.getSkin()).getBehavior().traversePrevious();  
        }
    }
    public static void SetPreviousFocus(CheckBox foField){
        if( foField.getSkin() instanceof BehaviorSkinBase) {
            ((BehaviorSkinBase) foField.getSkin()).getBehavior().traversePrevious();  
        }
    }
    
    public static void closeStage(Button foButton){
        Stage stage = (Stage) foButton.getScene().getWindow();
        stage.close();
    }
    
    public static void minimizeStage(Button foButton){
        Stage stage = (Stage) foButton.getScene().getWindow();
        stage.setIconified(true);
    }
}
