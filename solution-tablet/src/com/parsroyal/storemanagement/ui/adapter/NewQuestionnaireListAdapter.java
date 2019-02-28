package com.parsroyal.storemanagement.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.constants.Constants;
import com.parsroyal.storemanagement.data.listmodel.QuestionnaireListModel;
import com.parsroyal.storemanagement.service.QuestionnaireService;
import com.parsroyal.storemanagement.service.impl.QuestionnaireServiceImpl;
import com.parsroyal.storemanagement.ui.activity.MainActivity;
import com.parsroyal.storemanagement.ui.adapter.NewQuestionnaireListAdapter.ViewHolder;
import com.parsroyal.storemanagement.util.NumberUtil;
import java.util.List;
import java.util.Locale;

/**
 * Created by shkbhbb on 10/14/17.
 */

public class NewQuestionnaireListAdapter extends
    Adapter<ViewHolder> {

  private Context context;
  private List<QuestionnaireListModel> questionnaires;
  private LayoutInflater inflater;
  private MainActivity mainActivity;
  private Bundle args;
  private QuestionnaireService questionnaireService;

  public NewQuestionnaireListAdapter(Context context, List<QuestionnaireListModel> questionnaires,
      Bundle args) {
    this.context = context;
    this.mainActivity = (MainActivity) context;
    this.args = args;
    this.questionnaireService = new QuestionnaireServiceImpl(mainActivity);
    this.questionnaires = questionnaires;
    inflater = LayoutInflater.from(context);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_questionnaire_list, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    holder.setData(position);
  }

  @Override
  public int getItemCount() {
    return questionnaires.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.questionnaire_size_tv)
    TextView questionnaireSizeTv;
    @BindView(R.id.main_lay)
    RelativeLayout mainLay;

    private QuestionnaireListModel questionnaire;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      mainLay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
      switch (v.getId()) {
        case R.id.main_lay:

          args.putLong(Constants.QUESTIONNAIRE_BACKEND_ID, questionnaire.getBackendId());
          args.putSerializable(Constants.QUESTIONNAIRE_OBJ, questionnaire);

          args.putLong(Constants.GOODS_GROUP_BACKEND_ID, questionnaire.getGoodsGroupBackendId());

          mainActivity.changeFragment(MainActivity.QUESTION_LIST_FRAGMENT_ID, args, false);
          break;
      }
    }

    public void setData(int position) {
      this.questionnaire = questionnaires.get(position);
      titleTv.setText(questionnaire.getDescription());
      questionnaireSizeTv.setText(NumberUtil.digitsToPersian(
          String.format(Locale.getDefault(), "%d سوال", questionnaire.getQuestionsCount())));
    }
  }
}