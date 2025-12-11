/*
Extensão MangaLivre para Mihon/Tachiyomi
*/

package eu.kanade.tachiyomi.extension.pt.mangalivre

import eu.kanade.tachiyomi.source.model.*
import eu.kanade.tachiyomi.source.source.*
import okhttp3.*
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.lang.Exception

class MangaLivre : ConfigurableSource, ParsedHttpSource() {

    // === CONFIGURAÇÃO BÁSICA ===
    override val id: Long = 6969696969L // ID único para MangaLivre
    override val name: String = "MangaLivre" // nome da fonte
    override val baseUrl: String = "https://mangalivre.blog" // domain/base
    override val lang: String = "pt-BR"
    override val supportsLatest: Boolean = true

    // === HEADERS E CLIENT ===
    private val defaultUserAgent = "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 Chrome/122.0.0.0 Mobile Safari/537.36"

    override fun headersBuilder(): Headers.Builder = Headers.Builder()
        .add("User-Agent", defaultUserAgent)
        .add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")

    override val client: OkHttpClient = network.client.newBuilder()
        .build()

    // === PAGINAÇÃO / POPULARES / RECENTES / BUSCA ===

    override fun popularMangaRequest(page: Int): Request {
        val url = "$baseUrl/manga/?orderby=rating&paged=$page" // Mangás populares por rating
        return GET(url, headers)
    }

    override fun popularMangaSelector(): String = "div.post-card" // Card de cada mangá

    override fun popularMangaFromElement(element: Element): SManga {
        val manga = SManga.create()
        manga.title = element.select("h3.post-title").text() // Título do mangá
        manga.setUrlWithoutDomain(element.select("a.post-link").attr("href"))
        manga.thumbnail_url = element.select("img.post-image").attr("src")
        return manga
    }

    override fun popularMangaNextPageSelector(): String? = "a.next" // Próxima página

    // Latest (novidades)
    override fun latestUpdatesRequest(page: Int): Request {
        val url = "$baseUrl/page/$page/" // Últimas atualizações
        return GET(url, headers)
    }

    override fun latestUpdatesSelector(): String = "div.post-card" // Card de cada mangá

    override fun latestUpdatesFromElement(element: Element): SManga = popularMangaFromElement(element)

    override fun latestUpdatesNextPageSelector(): String? = "a.next"

    // Busca
    override fun searchMangaRequest(page: Int, query: String, filters: FilterList): Request {
        // Se o site tiver busca via query string
        val url = HttpUrl.parse("$baseUrl/manga/")!!.newBuilder()
            .addQueryParameter("s", query)
            .addQueryParameter("paged", page.toString())
            .build()
        return GET(url.toString(), headers)
    }

    override fun searchMangaSelector(): String = "div.post-card" // Card de cada mangá
    override fun searchMangaFromElement(element: Element): SManga = popularMangaFromElement(element)
    override fun searchMangaNextPageSelector(): String? = "a.next"

    // === DETALHES, CAPÍTULOS E PÁGINAS ===

    override fun mangaDetailsParse(document: Document): SManga {
        val info = SManga.create()
        info.title = document.selectFirst("h1.manga-title")?.text() ?: "" // Título
        info.author = document.select("span.manga-author").text() // Autor
        info.description = document.select("div.manga-synopsis").text() // Sinopse
        info.thumbnail_url = document.selectFirst("img.manga-poster")?.attr("src")
        return info
    }

    override fun chapterListSelector(): String = "div.chapter-item" // Cada capítulo

    override fun chapterFromElement(element: Element): SChapter {
        val chapter = SChapter.create()
        chapter.name = element.select("a.chapter-link").text()
        chapter.setUrlWithoutDomain(element.select("a.chapter-link").attr("href"))
        val dateText = element.select("span.chapter-date").attr("datetime")
        chapter.date_upload = parseDate(dateText)
        return chapter
    }

    override fun pageListRequest(chapter: SChapter): Request {
        val url = if (chapter.url.startsWith("http")) chapter.url else baseUrl + chapter.url
        return GET(url, headers)
    }

    override fun pageListParse(document: Document): List<Page> {
        // Exemplo: imagens carregadas via <img class="manga-page" src="...">
        val pages = mutableListOf<Page>()
        val imgs = document.select("img.manga-page")
        for ((i, img) in imgs.withIndex()) {
            val imgUrl = img.attr("data-src").ifEmpty { img.attr("src") }
            pages.add(Page(i, "", imgUrl))
        }
        return pages
    }

    override fun imageUrlParse(document: Document): String = document.selectFirst("img.chapter-image")?.attr("src") ?: ""

    // === UTILITÁRIOS ===
    private fun parseDate(dateText: String?): Long {
        // TODO: implementar parsing de datas conforme o site (ex: ISO, pt-BR, timestamps)
        return try { 0L } catch (e: Exception) { 0L }
    }

    // === PREFERÊNCIAS CONFIGURÁVEIS PELO USUÁRIO ===
    override fun setupPreferenceScreen(screen: Any) {
        // screen.addPreference(EditTextPreference(...))
        // Nota: Implementação completa requer dependências androidx.preference
        // Este é um exemplo de como seria estruturado
    }

    // === FILTROS (opcional) ===
    override fun getFilterList(): FilterList = FilterList()

}
