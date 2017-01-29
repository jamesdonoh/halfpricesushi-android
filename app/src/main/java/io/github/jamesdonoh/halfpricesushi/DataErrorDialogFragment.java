package io.github.jamesdonoh.halfpricesushi;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class DataErrorDialogFragment extends DialogFragment {
    public interface DataErrorDialogListener {
        void onRetryClick();
    }

    private DataErrorDialogListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Verify that the hosting activity implements callback interface
        try {
            mListener = (DataErrorDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement DataErrorDialogListener");
        }
    }

    @Override @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.api_error)
                .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onRetryClick();
                    }
                });

        return builder.create();
    }
}