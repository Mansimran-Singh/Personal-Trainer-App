package com.lemedebug.personaltrainer

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lemedebug.personaltrainer.exercise.Exercise
import com.lemedebug.personaltrainer.exercise.ExerciseAdapter
import com.lemedebug.personaltrainer.exercise.ExerciseViewModel
import com.lemedebug.personaltrainer.utils.Constants
import kotlinx.android.synthetic.main.fragment_view_all_exercises.*

class ViewAllExercisesFragment : Fragment() {

    private val BASE_URL = "https://wger.de/api/v2/exerciseinfo/"
    private val TAG = "CREATE_WORKOUT_ACTIVITY"
    private var exerciseList = ArrayList<Exercise>()
//    private val adapter = ExerciseAdapter(exerciseList)
    private var tempList = ArrayList<Exercise>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        getData()

    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_view_all_exercises, container, false)

        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(view.findViewById(R.id.toolbar_view_all_exercises))
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        activity.supportActionBar?.title = "ADD EXERCISE"


        val sharedPreferences = activity.getSharedPreferences(Constants.PT_PREFERENCES, Context.MODE_PRIVATE)
        val exerciseStringList = sharedPreferences.getString(Constants.EXERCISES,"")

        val sType = object : TypeToken<List<Exercise>>() { }.type
        exerciseList.addAll(Gson().fromJson<List<Exercise>>(exerciseStringList,sType) as ArrayList<Exercise>)
        val adapter  = ExerciseAdapter(exerciseList)
        adapter.notifyDataSetChanged()


        view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_view_all_exercises).setNavigationOnClickListener {
            val viewModel = ViewModelProvider(requireActivity()).get(ExerciseViewModel::class.java)
            viewModel.exercise = null
            viewModel.reps = null
            requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.exercise_view_container, CreateWorkoutFragment())
                    .commit()
        }


        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_exercise_list)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val searchView = view.findViewById<SearchView>(R.id.searchView)
        val spinner_cat: Spinner = view.findViewById(R.id.spinner_category)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                exerciseList = tempList
                spinner_category.setSelection(0)
                adapter.getFilter().filter(newText)
                return false
            }

        })


        ArrayAdapter.createFromResource(
                requireActivity(),
                R.array.filterList,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner_cat.adapter = adapter
        }
        spinner_cat.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                exerciseList = tempList

                adapter.getFilter().filter(" ")}



            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //val selectedItem = parent?.getItemAtPosition(position).toString()
                val spinner_List = resources.getStringArray(R.array.filterList)
                var newText = spinner_List[position]
                if (newText == "None")
                    newText = ""

                adapter.getFilter().filter(newText)}

        }
        return view
    }


}