package fr.isep.zili62724.cryptoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Header
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import fr.isep.zili62724.cryptoapp.databinding.ActivityMainBinding
import fr.isep.zili62724.cryptoapp.databinding.RvItemBinding
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var rvAdapter: RvAdapter
    private lateinit var data: ArrayList<Model>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        data=ArrayList<Model>()
        apiData
        rvAdapter=RvAdapter(this,data)
        binding.Rv.layoutManager=LinearLayoutManager(this)
        binding.Rv.adapter=rvAdapter
        binding.search.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                val filterdata=ArrayList<Model>()
                for (item in data){
                    if(item.name.lowercase(Locale.getDefault()).contains(p0.toString().lowercase(Locale.getDefault())))
                    {
                        filterdata.add(item)
                    }

                }
                if(filterdata.isEmpty()){
                    Toast.makeText(this@MainActivity,"No data available",Toast.LENGTH_LONG).show()
                }else{
                    rvAdapter.changeData(filterdata)
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
        })
    }
    val apiData:Unit
        get(){
            val url="https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest"
            val queue=Volley.newRequestQueue(this)
            val jsonObjectRequest:JsonObjectRequest=
                object:JsonObjectRequest(Method.GET,url,null,Response.Listener {
                    response ->
                    binding.progressBar.isVisible=false
                    try{
                        val dataArray=response.getJSONArray("data")
                        for(i in 0 until dataArray.length())
                        {
                            val dataObject=dataArray.getJSONObject(i)
                            val symbol=dataObject.getString("symbol")
                            val name=dataObject.getString("name")
                            val quote=dataObject.getJSONObject("quote")
                            val USD=quote.getJSONObject("USD")
                            val price=String.format("$ "+"%.2f",USD.getDouble("price"))

                            data.add(Model(name,symbol,price.toString()))
                        }
                        rvAdapter.notifyDataSetChanged()
                    }catch (e:Exception){
                        Toast.makeText(this,"Error 2",Toast.LENGTH_LONG).show()
                    }
                },Response.ErrorListener {
                    Toast.makeText(this,"Error 1",Toast.LENGTH_LONG).show()
                })
                {
                    override fun getHeaders(): Map<String, String> {

                        val headers=HashMap<String,String>();
                        headers["X-CMC_PRO_API_KEY"]="3aefaa5b-0d4a-458b-b362-a4b2e1fa3d4b"
                        return headers
                    }

                }
            queue.add(jsonObjectRequest)
        }

}