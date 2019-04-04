package com.logicpd.papapill.data.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.logicpd.papapill.R;
import com.logicpd.papapill.data.Contact;
import com.logicpd.papapill.data.NotificationSetting;
import com.logicpd.papapill.data.User;
import com.logicpd.papapill.database.DatabaseHelper;
import com.logicpd.papapill.interfaces.OnButtonClickListener;
import com.logicpd.papapill.misc.AppConstants;
import com.logicpd.papapill.utils.TextUtils;

import java.util.List;

/**
 * Template for recyclerview list adapters
 *
 * @author alankilloren
 */
public class NotificationSettingsAdapter extends RecyclerView.Adapter<NotificationSettingsAdapter.DataObjectHolder> {
    private List<NotificationSetting> settings;
    private NotificationSettingsAdapter.MyClickListener myClickListener;
    private int selectedPosition = -1;
    private Context context;
    private OnButtonClickListener mListener;

    public NotificationSettingsAdapter(Context context, List<NotificationSetting> resultList, OnButtonClickListener mListener) {
        settings = resultList;
        this.context = context;
        this.mListener = mListener;
    }


    @NonNull
    @Override
    public NotificationSettingsAdapter.DataObjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_setting_item, parent, false);

        return new DataObjectHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NotificationSettingsAdapter.DataObjectHolder holder, int position) {

        holder.tvSettingName.setText(settings.get(position).getName().toUpperCase());

        //get associated contact
        holder.contact = DatabaseHelper.getInstance(context).getContact(settings.get(position).getContactId());
        holder.user = DatabaseHelper.getInstance(context).getUserByID(settings.get(position).getUserId());

        //show selected or not
        if (!settings.get(position).isTextSelected()) {
            holder.btnText.setSelected(false);
        } else {
            holder.btnText.setSelected(true);
        }
        if (!settings.get(position).isEmailSelected()) {
            holder.btnEmail.setSelected(false);
        } else {
            holder.btnEmail.setSelected(true);
        }
        if (!settings.get(position).isVoiceSelected()) {
            holder.btnVoice.setSelected(false);
        } else {
            holder.btnVoice.setSelected(true);
        }

        // check to see if text, voice and/or email is entered and adjust color of button, etc.

        //click listeners
        holder.btnText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //text pressed
                Log.d(AppConstants.TAG, "Text Pressed on item: " + settings.get(holder.getAdapterPosition()).getName().toUpperCase());
                if (holder.contact.getTextNumber() != null && !holder.contact.getTextNumber().isEmpty()) {
                    if (!v.isSelected()) {
                        v.setSelected(true);
                        settings.get(holder.getAdapterPosition()).setTextSelected(true);
                    } else if (v.isSelected()) {
                        v.setSelected(false);
                        settings.get(holder.getAdapterPosition()).setTextSelected(false);
                    }
                } else {
                    // open text number entry screen
                    TextUtils.showToast(context, "Please enter a text number");
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("user", holder.user);
                    bundle.putSerializable("contact", holder.contact);
                    bundle.putBoolean("isFromNotificationSettingsAdapter", true);
                    bundle.putString("fragmentName", "ContactTextNumberFragment");
                    mListener.onButtonClicked(bundle);
                }
            }
        });
        holder.btnVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //voice pressed
                Log.d(AppConstants.TAG, "Voice Pressed on item: " + settings.get(holder.getAdapterPosition()).getName().toUpperCase());
                if (holder.contact.getVoiceNumber() != null && !holder.contact.getVoiceNumber().isEmpty()) {
                    if (!v.isSelected()) {
                        v.setSelected(true);
                        settings.get(holder.getAdapterPosition()).setVoiceSelected(true);
                    } else if (v.isSelected()) {
                        v.setSelected(false);
                        settings.get(holder.getAdapterPosition()).setVoiceSelected(false);
                    }
                } else {
                    // open voice number entry screen
                    TextUtils.showToast(context, "Please enter a voice number");
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("user", holder.user);
                    bundle.putSerializable("contact", holder.contact);
                    bundle.putBoolean("isFromNotificationSettingsAdapter", true);
                    bundle.putString("fragmentName", "ContactVoiceNumberFragment");
                    mListener.onButtonClicked(bundle);
                }
            }
        });
        holder.btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //email pressed
                Log.d(AppConstants.TAG, "Email Pressed on item: " + settings.get(holder.getAdapterPosition()).getName().toUpperCase());
                if (holder.contact.getEmail() != null && !holder.contact.getEmail().isEmpty()) {
                    if (!v.isSelected()) {
                        v.setSelected(true);
                        settings.get(holder.getAdapterPosition()).setEmailSelected(true);
                    } else if (v.isSelected()) {
                        v.setSelected(false);
                        settings.get(holder.getAdapterPosition()).setEmailSelected(false);
                    }
                } else {
                    // open email entry screen
                    TextUtils.showToast(context, "Please enter an email address");
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("user", holder.user);
                    bundle.putSerializable("contact", holder.contact);
                    bundle.putBoolean("isFromNotificationSettingsAdapter", true);
                    bundle.putString("fragmentName", "ContactEmailAddressFragment");
                    mListener.onButtonClicked(bundle);
                }
            }
        });
    }

    public List<NotificationSetting> getListFromAdapter() {
        return settings;
    }

    @Override
    public int getItemCount() {
        return settings.size();
    }

    public class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvSettingName;
        Button btnText, btnVoice, btnEmail;
        Contact contact;
        User user;
        //CheckBox checkSelected;

        DataObjectHolder(View itemView) {
            super(itemView);
            tvSettingName = itemView.findViewById(R.id.textview_notification_setting_name);
            btnText = itemView.findViewById(R.id.button_notification_text);
            btnVoice = itemView.findViewById(R.id.button_notification_voice);
            btnEmail = itemView.findViewById(R.id.button_notification_email);
            //checkSelected = itemView.findViewById(R.id.checkbox_contact);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
            selectedPosition = getAdapterPosition();
            notifyDataSetChanged();
        }
    }

    public void setOnItemClickListener(NotificationSettingsAdapter.MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public interface MyClickListener {
        void onItemClick(int position, View v);
    }
}
