package com.logicpd.papapill.data.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.logicpd.papapill.R;
import com.logicpd.papapill.data.Contact;
import com.logicpd.papapill.database.DatabaseHelper;

import java.util.List;

/**
 * Template for recyclerview list adapters
 *
 * @author alankilloren
 */
public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.DataObjectHolder> {
    private List<Contact> contacts;
    private ContactsAdapter.MyClickListener myClickListener;
    private int selectedPosition = -1;
    private boolean isNotificationContacts;
    private Context context;

    public ContactsAdapter(Context context, List<Contact> resultList, Boolean isNotificationContacts) {
        this.contacts = resultList;
        this.isNotificationContacts = isNotificationContacts;
        this.context = context;
    }


    @NonNull
    @Override
    public ContactsAdapter.DataObjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_item, parent, false);

        ContactsAdapter.DataObjectHolder dataObjectHolder = new ContactsAdapter.DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ContactsAdapter.DataObjectHolder holder, int position) {

        holder.tvContactName.setText(contacts.get(position).getName());

        if (isNotificationContacts) {
            if (contacts.get(position).isSelected()) {
                //display checkmark and remove button if contact is selected
                holder.checkSelected.setChecked(true);
                holder.btnRemove.setVisibility(View.VISIBLE);
                holder.btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO should I present a yes/no dialog box here?
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Are you sure you want to remove notifications for this contact?")
                                .setCancelable(false)
                                .setPositiveButton("Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                                // setSelected to false for contact
                                                contacts.get(holder.getAdapterPosition()).setSelected(false);
                                                holder.btnRemove.setVisibility(View.GONE);
                                                notifyDataSetChanged();
                                                // update db
                                                DatabaseHelper db = new DatabaseHelper((Activity) context);
                                                db.updateContact(contacts.get(holder.getAdapterPosition()));
                                                //TODO should I also remove notification settings for this contact?
                                            }
                                        })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();

                    }
                });
            } else {
                holder.checkSelected.setChecked(false);
                holder.btnRemove.setVisibility(View.GONE);
            }
        } else {
            holder.checkSelected.setVisibility(View.GONE);
            holder.btnRemove.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvContactName;
        CheckBox checkSelected;
        Button btnRemove;

        public DataObjectHolder(View itemView) {
            super(itemView);
            tvContactName = itemView.findViewById(R.id.textview_contact_name);
            checkSelected = itemView.findViewById(R.id.checkbox_contact);
            btnRemove = itemView.findViewById(R.id.button_remove_contact);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
            selectedPosition = getAdapterPosition();
            notifyDataSetChanged();
        }
    }

    public void setOnItemClickListener(ContactsAdapter.MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public interface MyClickListener {
        void onItemClick(int position, View v);
    }
}