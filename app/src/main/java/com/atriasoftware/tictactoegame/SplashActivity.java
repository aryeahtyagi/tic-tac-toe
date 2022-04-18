package com.atriasoftware.tictactoegame;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.PurchaseInfo;

public class SplashActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {

    private static final String TAG = "SplashActivity" ;
    private BillingProcessor billingProcessor = null;
    private PurchaseInfo purchaseInfo = null;

    private String product_id = "tictactoebilling";
    private String license_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAv8g1ZlP/fonNUiYfHoQBg3qZMIZuJs6kZz0kLgsJzj1rgfuHt+GUGAuKctIZrqWKXKD+hC5qItZ574FD/HfGEz7atik6qxtL5StXj+PTtmfyUTJXtEe1/aN3z1c9a+g/3RGH9sNNZ9tf9I445L+diw4kMQDlPaUXuSB5Vw2mMYymqxCOvNR77CDYYVXw7yJ7Yb3v7UQFcclCXPP0bKgMM2rTjR+lDscQgP9qHiY2oJjPaoVg8DTW5TL/rKBEEcJffqwEPN7GuQdrwbv7jEy+6oAZe9UMSyahAJZPT3E+xbcWsmlz0Nl3Pf3dVHpAPhRcFXl5qsXK3mavbTssIZC1HQIDAQAB";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Activity activity = this;

        billingProcessor = new BillingProcessor(this, license_key, this);
        billingProcessor.initialize();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                billingProcessor.subscribe(activity, product_id);
            }
        }, 2000);

    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable PurchaseInfo details) {
        Toast.makeText(this, "Successfully Purchased", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onPurchaseHistoryRestored() {
        billingProcessor.loadOwnedPurchasesFromGoogleAsync(new  BillingProcessor.IPurchasesResponseListener(){
            @Override
            public void onPurchasesSuccess() {
                Log.i(TAG, "onPurchasesSuccess: ");
            }

            @Override
            public void onPurchasesError() {
                Log.i(TAG, "onPurchasesError: ");
            }

        });

        if(billingProcessor.getSubscriptionPurchaseInfo(product_id) != null) {
            purchaseInfo = billingProcessor.getSubscriptionPurchaseInfo(product_id);
            if(purchaseInfo!=null) {
                if (purchaseInfo.purchaseData.autoRenewing) {
                    Intent intent = new Intent(this,OfflineGameMenuActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
                else{
                    Log.i(TAG, "onPurchaseHistoryRestored: Not renewing");
                }
            }else{
                Log.i(TAG, "onPurchaseHistoryRestored: null purchase ");
            }
        }else{
            Log.i(TAG, "onPurchaseHistoryRestored: null subscription");
        }
    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
        if (error != null) {
            Log.i(TAG, "onBillingError: "+error.getMessage());
        }
    }

    @Override
    public void onBillingInitialized() {

    }

    @Override
    protected void onDestroy() {
        billingProcessor.release();
        super.onDestroy();
    }

}