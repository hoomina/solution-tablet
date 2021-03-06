package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.PageStatus;
import com.parsroyal.solutiontablet.data.dao.QuestionnaireDao;
import com.parsroyal.solutiontablet.data.dao.impl.QuestionnaireDaoImpl;
import com.parsroyal.solutiontablet.data.listmodel.QuestionnaireListModel;
import com.parsroyal.solutiontablet.data.searchobject.QuestionnaireSo;
import com.parsroyal.solutiontablet.service.impl.QuestionnaireServiceImpl;
import com.parsroyal.solutiontablet.ui.activity.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.AllQuestionnaireAdapter;
import com.parsroyal.solutiontablet.util.Empty;
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
