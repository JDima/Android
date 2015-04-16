package com.sbpmap.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;

import com.sbpmap.MainActivity;
import com.sbpmap.Map.WebPlaceFinder;
import com.sbpmap.R;

import java.util.ArrayList;


public class AlertDialogManager {
	 @SuppressWarnings("deprecation")
	public static void showAlertDialog(Context context, String title, String message,
	            Boolean status) {
         AlertDialog alertDialog = new AlertDialog.Builder(context).create();

         alertDialog.setTitle(title);

         alertDialog.setMessage(message);

         if (status != null)
             alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);

         alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface dialog, int which) {
             }
         });
         alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
         alertDialog.show();
     }

    public void showSelectCategoryDialog(final Context context, final WebPlaceFinder fp, final double lat, final double lng, final LatLngBounds latLngBounds) {
        final AlertDialog dialog;
        Log.d("Java log", "showSelectCategoryDialog()");
        final String notSelected, titleSelectCategory, error, search, cancel;

        notSelected = MainActivity.isEnglish ? "You did not select categories!" : "Вы не выбрали категории!";
        titleSelectCategory = MainActivity.isEnglish ? "Select categories" : "Выберите категории!";
        error = MainActivity.isEnglish ? "Error!" : "Ошибка!";
        search = MainActivity.isEnglish ? "Search" : "Поиск";
        cancel = MainActivity.isEnglish ? "Cancel" : "Отменить";

        final ArrayList<Integer> seletedItems = new ArrayList<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(titleSelectCategory);

        builder.setMultiChoiceItems(WebPlaceFinder.VENUES, null,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected,
                                        boolean isChecked) {
                        if (isChecked) {
                            seletedItems.add(indexSelected);
                        } else if (seletedItems.contains(indexSelected)) {
                            seletedItems.remove(Integer.valueOf(indexSelected));
                        }
                    }
                })
                .setPositiveButton(search, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                    }
                })
                .setNegativeButton(cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                    }
                });


        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (seletedItems.isEmpty()) {
                    AlertDialogManager.showAlertDialog(context, error,
                            notSelected, false);
                } else {
                    fp.searchPlaces(lat, lng, seletedItems, latLngBounds);
                    dialog.dismiss();
                }
            }
        });

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });

    }

    public void connectionError(Context context) {
        String title, msg;
        if (MainActivity.isEnglish) {
            title = "Internet Connection Error";
            msg = "Please connect to Internet!";
        } else {
            title = "Ошибка интернет соединения";
            msg = "Подключитесь к интернету!";
        }
        AlertDialogManager.showAlertDialog(context, title,
                msg, false);
    }
}
