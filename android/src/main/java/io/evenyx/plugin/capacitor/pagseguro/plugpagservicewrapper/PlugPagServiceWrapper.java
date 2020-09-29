package io.evenyx.plugin.capacitor.pagseguro.plugpagservicewrapper;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPag;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagActivationData;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagAppIdentification;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagCustomPrinterLayout;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagInitializationResult;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagPaymentData;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagPrintResult;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagTransactionResult;
import dagger.Provides;

@NativePlugin(
        requestCodes={PlugPagServiceWrapper.REQUEST_CONTACTS}
)
public class PlugPagServiceWrapper extends Plugin {

    protected static final int REQUEST_CONTACTS = 12345; // Unique request code

    @Provides
    PlugPag mPlugPag() {
        Context context;

        context = getContext();

        PlugPag plugPag = new PlugPag(context,new PlugPagAppIdentification("TESTE", "1"));

        return plugPag;
    }

    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", value);
        call.success(ret);
    }

    @PluginMethod()
    public void getContacts(PluginCall call) {
        String value = call.getString("filter");
        // Filter based on the value if want

        saveCall(call);
        pluginRequestPermission(Manifest.permission.READ_CONTACTS, REQUEST_CONTACTS);
    }

    @PluginMethod
    public void getLibVersion(PluginCall call) {
        String value = call.getString("value");

        String results = mPlugPag().getLibVersion();

        JSObject ret = new JSObject();
        ret.put("results", results);
        call.success(ret);

    }

    @PluginMethod
    public void reprintStablishmentReceipt(PluginCall call) {
        String value = call.getString("value");


        PlugPagPrintResult results = mPlugPag().reprintStablishmentReceipt();

        JSObject ret = new JSObject();
        ret.put("results", results);
        call.success(ret);
    }

    @PluginMethod
    public void reprintCustomerReceipt(PluginCall call) {
        String value = call.getString("value");

        PlugPagPrintResult results = mPlugPag().reprintCustomerReceipt();

        JSObject ret = new JSObject();
        ret.put("results", results);
        call.success(ret);
    }

    @PluginMethod
    public void getLastApprovedTransaction(PluginCall call) {
        String value = call.getString("value");

        PlugPagTransactionResult results = mPlugPag().getLastApprovedTransaction();

        JSObject ret = new JSObject();
        ret.put("results", results);
        call.success(ret);
    }

    @PluginMethod 
    public void startPayment(PluginCall call) {

        PlugPagPaymentData paymentData =
            new PlugPagPaymentData(
                    PlugPag.TYPE_CREDITO,
                    41000,
                    PlugPag.INSTALLMENT_TYPE_A_VISTA,
                    1,
                    "ABCDEF1234",
                    true);

        PlugPagCustomPrinterLayout customDialog = new PlugPagCustomPrinterLayout();
        customDialog.setTitle("Imprimir via do cliente?");
        customDialog.setButtonBackgroundColor("#00ff33");
        customDialog.setConfirmTextColor("Yes");
        customDialog.setCancelTextColor("No");

        PlugPagInitializationResult initResult = mPlugPag().initializeAndActivatePinpad(new
                PlugPagActivationData("403938"));

        if (initResult.getResult() == PlugPag.RET_OK) {

            mPlugPag().setPlugPagCustomPrinterLayout(customDialog);
            PlugPagTransactionResult results = mPlugPag().doPayment(paymentData);

            JSObject ret = new JSObject();
            ret.put("results", results);
            call.success(ret);
        } else {
            JSObject ret = new JSObject();
            ret.put("results", initResult.getResult());
            call.success(ret);
        }

    }

    @Override
    protected void handleRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.handleRequestPermissionsResult(requestCode, permissions, grantResults);


        PluginCall savedCall = getSavedCall();
        if (savedCall == null) {
            Log.d("Test", "No stored plugin call for permissions request result");
            return;
        }

        for(int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                Log.d("Test", "User denied permission");
                return;
            }
        }

        if (requestCode == REQUEST_CONTACTS) {
            // We got the permission!
            loadContacts(savedCall);
        }
    }

    void loadContacts(PluginCall call) {
        ArrayList<Map> contactList = new ArrayList<>();
        ContentResolver cr = this.getContext().getContentResolver();

        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                Map<String,String> map =  new HashMap<String, String>();

                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                map.put("firstName", name);
                map.put("lastName", "");

                String contactNumber = "";

                if (cur.getInt(cur.getColumnIndex( ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    pCur.moveToFirst();
                    contactNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    Log.i("phoneNUmber", "The phone number is "+ contactNumber);
                }
                map.put("telephone", contactNumber);
                contactList.add(map);
            }
        }
        if (cur != null) {
            cur.close();
        }

        JSONArray jsonArray = new JSONArray(contactList);
        JSObject ret = new JSObject();
        ret.put("results", jsonArray);
        call.success(ret);
    }

}
