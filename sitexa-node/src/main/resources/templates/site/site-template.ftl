
<#macro mainLayout title="Site">
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

    <title>${title} | Sweet</title>
    <link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/pure/0.6.0/pure-min.css">
    <link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/pure/0.6.0/grids-responsive-min.css">
    <link rel="stylesheet" type="text/css" href="/public/styles/sweet.css">
    <link rel="stylesheet" type="text/css" href="/public/styles/styles.css">
    <link rel="stylesheet" type="text/css" href="/public/node/styles.css" media="screen">
</head>
<body>
<div class="pure-g">
    <div class="sidebar pure-u-1 pure-u-md-1-4">
        <div class="header" style="margin-top: 10px">
            <div class="pure-img-responsive">
                <img src="/public/images/logo.png">
            </div>
            <div class="brand-title">Sweet</div>
            <nav class="nav">
                <ul class="nav-list">
                    <li class="nav-item"><a class="pure-button" href="/">home</a></li>
                    <li class="nav-item"><a class="pure-button" href="/site/1">site by id</a></li>
                    <li class="nav-item"><a class="pure-button" href="/siteByCode/11">site by code</a></li>
                    <li class="nav-item"><a class="pure-button" href="/siteByLevel/1">site by level</a></li>
                    <li class="nav-item"><a class="pure-button" href="/childrenById/25579">children by id</a></li>
                    <li class="nav-item"><a class="pure-button" href="/childrenByCode/43">children by code</a></li>
                    <li class="nav-item"><a class="pure-button" href="/updateSiteLatLng/4301">update site lat lng</a></li>
                </ul>
            </nav>
        </div>

        <div class="footer">
            Sitexa Sweetie, ${.now?string("yyyy")}
        </div>
    </div>

    <div class="content pure-u-1 pure-u-md-3-4">
        <h2>${title}</h2>
        <#nested />
    </div>

</div>
</body>
</html>
</#macro>

<#-- @ftlvariable name="site" type="java.util.List<com.sitexa.ktor.model.Site>" -->
<#macro site_li site>
<section class="post">
    <header class="post-header">
        <p class="post-meta">
            ${site.name}
        </p>
    </header>
    <div class="post-description">
        ${site.id}:${site.code}:${site.name}:${site.parentId}:${site.level}:${site.lat?has_content?then(site.lat,"")}:${site.lng?has_content?then(site.lng,"")}
    </div>
</section>
</#macro>

<#macro sites_list sites>
<ul>
    <#list sites as site>
        <@site_li site=site></@site_li>
    <#else>
        <li>There are no site yet</li>
    </#list>
</ul>
</#macro>