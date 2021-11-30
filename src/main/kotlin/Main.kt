import java.net.ProxySelector
import java.util.Optional
import org.apache.http.client.config.RequestConfig
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import org.apache.http.impl.conn.SystemDefaultRoutePlanner
import org.apache.http.message.BasicHeader
import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.SolrRequest.METHOD.POST
import org.apache.solr.client.solrj.impl.CloudSolrClient
import org.apache.solr.common.util.Base64

fun main(args: Array<String>) {
    val builder = CloudSolrClient.Builder(
        listOf("lb-solr-btevx-live5-mcc-be-gcw1.metroscales.io:2181"),
        Optional.empty()
    )

    val requestConfig = RequestConfig.custom()
        .setConnectionRequestTimeout(500)
        .setSocketTimeout(500)
        .setConnectTimeout(500)
        .setRedirectsEnabled(false)
        .build()
    val httpClientBuilder = HttpClients
        .custom()
        .setConnectionManager(PoolingHttpClientConnectionManager())
        .setRoutePlanner(SystemDefaultRoutePlanner(ProxySelector.getDefault()))
        .setDefaultRequestConfig(requestConfig)


    val userPass = "solr" + ":" + "PASSWORD"
    val bytes = userPass.toByteArray()
    val encoded = Base64.byteArrayToBase64(bytes, 0, bytes.size)
    httpClientBuilder.setDefaultHeaders(listOf(BasicHeader("Authorization", "Basic $encoded")))

    val httpClient = httpClientBuilder.build()

    builder.withHttpClient(
        httpClient
    )
    val solrClient = builder.build()
    solrClient.defaultCollection = "betty-evaluate-bundles"

    solrClient.query(SolrQuery("*:*"), POST)
}