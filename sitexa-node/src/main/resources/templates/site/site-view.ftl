<#import "site-template.ftl" as layout />

<@layout.mainLayout title="Site view">

<#-- @ftlvariable name="media" type="com.sitexa.ktor.model.Site" -->
<section class="post">
    <@layout.site_li site=site></@layout.site_li>
</section>

</@layout.mainLayout>