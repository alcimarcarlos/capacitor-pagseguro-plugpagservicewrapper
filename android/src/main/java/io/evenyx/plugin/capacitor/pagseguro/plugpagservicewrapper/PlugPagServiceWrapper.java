package io.evenyx.plugin.capacitor.pagseguro.plugpagservicewrapper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.util.Log;

import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPag;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagActivationData;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagAppIdentification;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagCustomPrinterLayout;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagInitializationResult;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagPaymentData;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagPrintResult;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagPrinterData;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagTransactionResult;
import dagger.Provides;

@NativePlugin(

)

public class PlugPagServiceWrapper extends Plugin {

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

    @PluginMethod
    public void getLibVersion(PluginCall call) {

        String results = mPlugPag().getLibVersion();

        JSObject ret = new JSObject();
        ret.put("results", results);
        call.success(ret);

    }

    @PluginMethod
    public void reprintStablishmentReceipt(final PluginCall call) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle( "Atenção" )
                .setIcon(android.R.drawable.ic_dialog_info)
                .setMessage("Deseja imprimir a 2ª via do Estabelecimento?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {

                        dialoginterface.dismiss();

                        PlugPagPrintResult results = mPlugPag().reprintStablishmentReceipt();

                        JSObject ret = new JSObject();
                        ret.put("results", results);
                        call.success(ret);
                    }
                })
                .setNegativeButton("No", null).show();
    }

    @PluginMethod
    public void reprintCustomerReceipt(final PluginCall call) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle( "Atenção" )
                .setIcon(android.R.drawable.ic_dialog_info)
                .setMessage("Deseja imprimir a 2ª via do Cliente?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {

                        dialoginterface.dismiss();

                        PlugPagPrintResult results = mPlugPag().reprintCustomerReceipt();

                        JSObject ret = new JSObject();
                        ret.put("results", results);
                        call.success(ret);
                    }
                })
                .setNegativeButton("No", null).show();


    }

    @PluginMethod
    public void getLastApprovedTransaction(PluginCall call) {

        PlugPagTransactionResult results = mPlugPag().getLastApprovedTransaction();

        JSObject ret = new JSObject();
        ret.put("results", results);
        call.success(ret);
    }

    @PluginMethod
    public void printTicket(PluginCall call) throws IOException {

        FileUtils.copyURLToFile(
                new URL("https://evenyx-www.s3.amazonaws.com/assets/images/TICKET-FRENTE.jpg"),
                new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/TKT.jpg"));

        PlugPagPrintResult result = mPlugPag().printFromFile(new PlugPagPrinterData(
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/TKT.jpg",
                4,
                50));

        FileUtils.fileDelete(Environment.getExternalStorageDirectory().getAbsolutePath() + "/TKT.jpg");

        Log.d("PlugPagPrintResult", result.getMessage());

        JSObject ret = new JSObject();
        ret.put("results", result);
        call.success(ret);
    }

    @PluginMethod 
    public void createPayment(PluginCall call) {

            PlugPagPaymentData paymentData = null;

            String reference = call.getString("reference");
            String type = call.getString("type");
            Integer installments = call.getInt("installments");
            Integer amount = call.getInt("amount");

            Log.v("Evenyx TYPE", type);

            if (type.equals("DEBIT")) {
                paymentData = new PlugPagPaymentData(
                        PlugPag.TYPE_DEBITO,
                        amount,
                        PlugPag.INSTALLMENT_TYPE_A_VISTA,
                        1,
                        reference,
                        true);
            }

            if (type.equals("CREDIT")) {

                paymentData = new PlugPagPaymentData(
                        PlugPag.TYPE_CREDITO,
                        amount,
                        PlugPag.INSTALLMENT_TYPE_PARC_COMPRADOR,
                        installments,
                        reference,
                        true);

            }
            if (paymentData!=null) {

                PlugPagCustomPrinterLayout customDialog = new PlugPagCustomPrinterLayout();
                customDialog.setTitle("Imprimir via do Cliente?");
                customDialog.setButtonBackgroundColor("#00ff33");
                customDialog.setConfirmTextColor("Yes");
                customDialog.setCancelTextColor("No");

                PlugPagInitializationResult initResult = mPlugPag().initializeAndActivatePinpad(new
                        PlugPagActivationData("403938"));

                if (initResult.getResult() == PlugPag.RET_OK) {

                    try {

                        mPlugPag().setPlugPagCustomPrinterLayout(customDialog);

                        PlugPagTransactionResult results = mPlugPag().doPayment(paymentData);

                        JSObject ret = new JSObject();
                        ret.put("results", results);
                        call.success(ret);

                    } catch (Exception e) {

                        e.printStackTrace();

                    }


                } else {

                    JSObject ret = new JSObject();
                    ret.put("results", initResult.getResult());
                    call.success(ret);

                }
            }

    }

}
