package by.bsuir.quickqr.ui.scanner

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import by.bsuir.quickqr.R
import com.google.zxing.integration.android.IntentIntegrator

class ScannerViewModel : ViewModel() {

    private val _scannedResult = MutableLiveData<String>()
    val scannedResult: LiveData<String>
        get() = _scannedResult

    fun processScannedResult(result: String) : Boolean {
        _scannedResult.value = result
        return isUri(result)
    }

    fun isUri(result: String) : Boolean {
        return result.startsWith("http://") || result.startsWith("https://")
    }

    fun copyScannedResultToClipboard(context: Context) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Scanned Result", scannedResult.value)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show()
    }

    fun openScannedResultInBrowser(context: Context) {
        val result = scannedResult.value
        if (result?.startsWith("http://") == true || result?.startsWith("https://") == true) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(result)
            context.startActivity(intent)
        }
    }

    fun initiateScan(fragment: Fragment) {
        val integrator = IntentIntegrator.forSupportFragment(fragment)
        integrator.setBeepEnabled(false)
        integrator.setPrompt(fragment.getString(R.string.scan_prompt))
        integrator.setOrientationLocked(false)
        integrator.setPrompt("Scan a QR code")
        integrator.initiateScan()
    }

    fun showSharingDialog(context: Context) {
        val intent= Intent()
        intent.action=Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_TEXT,scannedResult.value)
        intent.type="text/plain"
        context.startActivity(Intent.createChooser(intent,"Share To:"))
    }

    fun searchInBrowser(context: Context) {
        val intent = Intent(Intent.ACTION_VIEW)
        val url = "https://www.google.com/search?q=${scannedResult.value}"
        intent.data = Uri.parse(url)
        context.startActivity(intent)
    }
}
