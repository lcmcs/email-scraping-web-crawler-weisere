package edu.touro.cs.mcon364;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class ScrapeURL implements Runnable {
    private String url;
    private Set<Email> emailList;
    private List<String> urlList;

    public ScrapeURL(String url) {
        this.url = url;
        emailList = new HashSet<>();
        urlList = new ArrayList<>();
    }

    public String getUrl(){
        return this.url;
    }

    @Override
    public void run() {
        scrapeLinksAndEmails();
    }

     public void scrapeLinksAndEmails() {
        try {
            Document document = Jsoup.connect(url).get();

            // Find all anchor tags and extract URLs
            Elements links = document.select("a[href]");

            for (Element link : links) {
                // Convert relative links to absolute links
                String absoluteUrl = link.absUrl("href");
                // If statement to ensure it doesn't include tel: and mailto:
                if (!link.attr("href").startsWith("tel:") && !link.attr("href").startsWith("mailto:")) {
                    if (absoluteUrl.isEmpty()) {
                        absoluteUrl = url + link.attr("href");
                    }

                    urlList.add(absoluteUrl);
                }
            }

            Pattern pattern = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+");
            Matcher matcher = pattern.matcher(document.toString());

            // Find all matches and add them to the list
            while (matcher.find()) {
                String email = matcher.group();
                email = email.toLowerCase().trim();
                emailList.add(new Email(email, url, LocalDateTime.now()));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid URL: " + e.getMessage());
            // Handle the exception or perform any necessary cleanup
        }
    }

    public Set<Email> getEmailList() {
        return emailList;
    }

    public List<String> getUrlList() {
        return urlList;
    }
}

