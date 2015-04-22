package edu.amd.spbstu.sbpmap.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import edu.amd.spbstu.sbpmap.MainActivity;
import edu.amd.spbstu.sbpmap.Map.WebPlaceFinder;
import edu.amd.spbstu.sbpmap.R;

import java.util.ArrayList;


public class AlertDialogManager {

    public static final String HOTEL = "Отель";
    public static final String HOSTEL = "Хостел";
    public static final String MINI_HOTEL = "Миниотель";
    public static final String LANDMARK = "Достопримечательность";
    public static final String BRIDGE = "Мост";
    public static final String PARK = "Парк";
    public static final String MONUMENT = "Монумент";
    public static final String RESTAURANT = "Ресторан";

    public static final String[] RU_VENUES = {RESTAURANT, HOTEL, LANDMARK, HOSTEL, MINI_HOTEL, MONUMENT, BRIDGE, PARK};

	@SuppressWarnings("deprecation")
    public AlertDialog alertDialog(Context context, String title, String message, int icon, boolean isTable) {
        QustomDialogBuilder qustomDialogBuilder = new QustomDialogBuilder(context, isTable);
        qustomDialogBuilder.setTitle(title);
        if (!isTable) {
            qustomDialogBuilder.setMessage(message);
        }
        qustomDialogBuilder.setIcon(icon);

        AlertDialog dialog = qustomDialogBuilder.create();
        dialog.setButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return dialog;
    }

    public static void paintButtons(Button button) {
        button.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff426088")));
        button.setTextColor(Color.parseColor("#ffffffff"));
        button.setTypeface(null, Typeface.BOLD);
    }

	public AlertDialog showAlertDialog(Context context, String title, String message, int icon, boolean isTable) {
        AlertDialog alertDialog = alertDialog(context, title, message, icon, isTable);
        if (!isTable) {
            alertDialog.show();

            paintButtons(alertDialog.getButton(DialogInterface.BUTTON_POSITIVE));
        }
        return alertDialog;
     }

    public AlertDialog showSelectCategoryDialog(final Context context, final WebPlaceFinder fp, final double lat, final double lng, final LatLngBounds latLngBounds) {
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
        builder.setCustomTitle(View.inflate(context, R.layout.custom_dialog_title, null));
        builder.setTitle(titleSelectCategory);
        builder.setMultiChoiceItems(MainActivity.isEnglish ? WebPlaceFinder.VENUES : RU_VENUES, null,
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
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (seletedItems.isEmpty()) {
                    showAlertDialog(context, error,
                            notSelected, R.drawable.fail, false);
                } else {
                    fp.removeAll();
                    fp.searchPlaces(lat, lng, seletedItems, latLngBounds);

                    dialog.dismiss();
                }
            }
        });
        paintButtons(dialog.getButton(AlertDialog.BUTTON_POSITIVE));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fp.returnPosition();
                dialog.dismiss();
            }
        });
        paintButtons(dialog.getButton(AlertDialog.BUTTON_NEGATIVE));

        //dialog.getListView().setBackgroundColor(Color.parseColor("#ff426088"));
        return dialog;
    }

    public AlertDialog connectionError(Context context) {
        String title, msg;
        if (MainActivity.isEnglish) {
            title = "Internet Connection Error";
            msg = "Phone does not have internet connection. Application will be close!";
        } else {
            title = "Ошибка интернет соединения";
            msg = "Телефон не имеет подключения к интернету. Приложение будет закрыто!";
        }
        return alertDialog(context, title,
                msg, R.drawable.fail, false);
    }
}
