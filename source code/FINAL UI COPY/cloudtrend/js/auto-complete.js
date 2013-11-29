    $(function() {
        $( "#languageSearch" ).autocomplete({
		alert("language");
            source: "languageTitles.php",
            minLength: 2,
            
        });
    });

$(function() {
        $( "#topic , #compareTopicFirst, #compareTopicSecond" ).autocomplete({
		alert("trendtopic");
            source: "trendTitles.php",
            minLength: 2,
            
        });
    });

