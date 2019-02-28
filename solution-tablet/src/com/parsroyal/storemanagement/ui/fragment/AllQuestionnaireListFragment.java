package com.parsroyal.storemanagement.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.constants.Constants;
import com.parsroyal.storemanagement.constants.PageStatus;
import com.parsroyal.storemanagement.data.dao.QuestionnaireDao;
import com.parsroyal.storemanagement.data.dao.impl.QuestionnaireDaoImpl;
import com.parsroyal.storemanagement.data.listmodel.QuestionnaireListModel;
import com.parsroyal.storemanagement.data.searchobject.QuestionnaireSo;
import com.parsroyal.storemanagement.service.impl.QuestionnaireServiceImpl;
import com.parsroyal.storemanagement.ui.activity.MainActivity;
import com.parsroyal.storemanagement.ui.adapter.AllQuestionnaireAdapter;
import com.parsroyal.storemanagement.util.Empty;
import java.util.List;

public class AllQuestionnaireListFragment extends BaseFragment {

  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;
  @BindView(R.id.add_questionnaire_fab)
  FloatingActionButton addQuestionnaireFab;
  Unbinder unbinder;

  private MainActivity mainActivity;
  private long customerBackendId;

  public AllQuestionnaireListFragment() {
    // Required empty public constructor
  }

  public static AllQuestionnaireListFragment newInstance(Bundle arguments) {
    AllQuestionnaireListFragment fragment = new AllQuestionnaireListFragment();
    fragment.setArguments(arguments);
    return fragment;
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_all_questionnaire_list, container, false);
    unbinder = ButterKnife.bind(this, view);
    mainActivity = (MainActivity) getActivity();

    setUpRecyclerView();
    return view;
  }

  private void setUpRecyclerView() {
    Bundle bundle = getArguments();
    if (Empty.isEmpty(bundle)) {
      bundle = new Bundle();
    }

    customerBackendId = bundle.getLong(Constants.CUSTOMER_BACKEND_ID, -1);
    if (customerBackendId == -1) {
      bundle.putInt(Constants.PARENT, MainActivity.REPORT_FRAGMENT);
      addQuestionnaireFab.setVisibility(View.GONE);
    } else {
      bundle.putInt(Constants.PARENT, MainActivity.CUSTOMER_INFO_FRAGMENT);
    }

    AllQuestionnaireAdapter allQuestionnaireAdapter = new AllQuestionnaireAdapter(mainActivity,
        getQuestionnaireList(), bundle);
    LayoutManager layoutManager = new LinearLayoutManager(mainActivity);
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setAdapter(allQuestionnaireAdapter);
  }

  private List<QuestionnaireListModel> getQuestionnaireList() {
    QuestionnaireSo questionnaireSo = new QuestionnaireSo();
    questionnaireSo.setCustomerBackendId(customerBackendId);
    QuestionnaireDao questionnaireService = new QuestionnaireDaoImpl(mainActivity);
    return questionnaireService.searchForQuestionsList(questionnaireSo);
  }

  @OnClick(R.id.add_questionnaire_fab)
  public void onViewClicked() {
    QuestionnaireServiceImpl questionnaireService = new QuestionnaireServiceImpl(mainActivity);
    Bundle bundle = getArguments();
    bundle.putInt(Constants.PARENT, MainActivity.CUSTOMER_INFO_FRAGMENT);
    bundle.putLong(Constants.ANSWERS_GROUP_NO, questionnaireService.getNextAnswerGroupNo());
    bundle.putSerializable(Constants.PAGE_STATUS, PageStatus.EDIT);
    mainActivity
        .changeFragment(MainActivity.QUESTIONNAIRE_CATEGORY_FRAGMENT_ID, bundle, true);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  @Override
  public int getFragmentId() {
    return MainActivity.ALL_QUESTIONNAIRE_FRAGMENT_ID;
  }
}