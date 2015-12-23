package com.wade.myfacedetection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {
    public static Activity  mMainActivity;
    public int startMode = 0;
    private MenuItem    mItemStart; // default
    private MenuItem    mItemProfileFace;
    private MenuItem    mItemAltTree;
    private MenuItem    mItemAlt2;
    private MenuItem    mItemAlt;
    private MenuItem    mItemExtended;
    private MenuItem    mItemCat;
    private MenuItem    mItemUpperBody;
    private MenuItem    mItemLowerBody;
    private MenuItem    mItemEye;
    private MenuItem    mItemEyeGlasses;
    private MenuItem    mItemLeftEye;
    private MenuItem    mItemRightEye;
    private MenuItem    mItemFullBody;
    private MenuItem    mItemRussian;
    private MenuItem    mItemSmile;
    private MenuItem    mItemPlate;

    public int getStartMode() { return startMode; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMainActivity = this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mItemStart = menu.add(R.string.start);
        mItemProfileFace = menu.add(R.string.ProfileFace);
        mItemAltTree = menu.add(R.string.AltTree);
        mItemAlt2 = menu.add(R.string.Alt2);
        mItemAlt = menu.add(R.string.Alt);
        mItemExtended = menu.add(R.string.Extended);
        mItemCat = menu.add(R.string.Cat);
        mItemCat = menu.add(R.string.UpperBody);
        mItemCat = menu.add(R.string.LowerBody);
        mItemCat = menu.add(R.string.FullBody);
        mItemCat = menu.add(R.string.Eye);
        mItemCat = menu.add(R.string.EyeGlasses);
        mItemCat = menu.add(R.string.LeftEye);
        mItemCat = menu.add(R.string.RightEye);
        mItemCat = menu.add(R.string.Russian);
        mItemCat = menu.add(R.string.Smile);
        mItemCat = menu.add(R.string.Plate);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item == mItemStart) { startMode = 0; }
        else if (item == mItemProfileFace) { startMode = 1; }
        else if (item == mItemAltTree) { startMode = 2; }
        else if (item == mItemAlt2) { startMode = 3; }
        else if (item == mItemAlt) { startMode = 4; }
        else if (item == mItemExtended) { startMode = 5; }
        else if (item == mItemCat) { startMode = 6; }
        else if (item == mItemUpperBody) { startMode = 7; }
        else if (item == mItemLowerBody) { startMode = 8; }
        else if (item == mItemFullBody) { startMode = 9; }
        else if (item == mItemEye) { startMode = 10; }
        else if (item == mItemEyeGlasses) { startMode = 11; }
        else if (item == mItemLeftEye) { startMode = 12; }
        else if (item == mItemRightEye) { startMode = 13; }
        else if (item == mItemRussian) { startMode = 14; }
        else if (item == mItemSmile) { startMode = 15; }
        else if (item == mItemPlate) { startMode = 16; }
        Intent intent = new Intent(MainActivity.this, FdActivity.class);
        startActivity(intent);
        return true;
    }
}
