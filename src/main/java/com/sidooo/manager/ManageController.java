package com.sidooo.manager;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.sidooo.ai.Attribute;
import com.sidooo.ai.Recognition;
import com.sidooo.division.DivisionService;
import com.sidooo.point.NetworkStatus;
import com.sidooo.point.Pagination;
import com.sidooo.point.PointService;
import com.sidooo.seed.Seed;
import com.sidooo.seed.SeedService;
import com.sidooo.sewing.TestResult;

@Controller
public class ManageController {

	@Autowired
	private SeedService seedService;

	@Autowired
	private DivisionService divisionService;

	@Autowired
	private PointService pointService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView defaultPage() {

		ModelAndView model = new ModelAndView();
		model.addObject("title", "Spring Security + Hibernate Example");
		model.addObject("message", "This is default page!");
		model.setViewName("hello");
		return model;

	}

	@RequestMapping(value = "/hello", method = RequestMethod.GET)
	public @ResponseBody String hello() {
		return "hello wmouth";
	}

	// @RequestMapping(value = "/division/CHN", method = RequestMethod.GET)
	// public void getDivisionList(HttpServletRequest request,
	// HttpServletResponse response) {
	//
	//
	// JSONArray result = divisionService.getDivisionOfCHN();
	// }

	@RequestMapping(value = "/seed/query", method = RequestMethod.GET)
	public @ResponseBody List<Seed> getSeedList() {

		List<Seed> seeds = seedService.getSeeds();
		return seeds;
	}

	 @RequestMapping(value = "/seed/create")
	 public @ResponseBody Seed createSeed(@RequestParam String seed) {
		 try {
			 seed = URLDecoder.decode(seed, "utf-8");
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
		 Gson gson = new Gson();
		 Seed newSeed = gson.fromJson(seed, Seed.class);
		 return seedService.createSeed(newSeed);
	 }

	@RequestMapping(value = "/seed/update")
	public @ResponseBody Seed updateSeed(@RequestParam String seed) {

		System.out.println(seed);
		try {
			seed = URLDecoder.decode(seed, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(seed);
		Gson gson = new Gson();
		Seed newSeed = gson.fromJson(seed, Seed.class);
		seedService.updateSeed(newSeed.getId(), newSeed);
		return newSeed;
	}

	@RequestMapping(value = "/seed/delete", method = RequestMethod.DELETE)
	public void deleteSeed(@RequestParam String id) {
		seedService.deleteSeed(id);
	}

	@RequestMapping(value = "/seed/toggle")
	public @ResponseBody boolean toggleSeed(@RequestParam String id) {
		return seedService.toggleSeed(id);
	}

	// @RequestMapping(value="/ontology/category/query",
	// method=RequestMethod.GET)
	// public @ResponseBody List<Category> getCategoryList() {
	// return ontoRepo.getCategroyList();
	// }

	// @RequestMapping(value="/ontology/category/create", method=
	// RequestMethod.GET)
	// public @ResponseBody String createCategory(@RequestParam String name) {
	// name = URLDecoder.decode(name, "utf-8");
	// Category category = new Category();
	// category.setName(name);
	// return ontoRepo.createCategory(category);
	// }

	// @RequestMapping(value="/ontology/category/update_name", method =
	// RequestMethod.GET)
	// public void updateCategoryName(
	// @RequestParam String id, @RequestParam String name) {
	// name = URLDecoder.decode(name, "utf-8");
	// ontoRepo.updateCategoryName(id, name);
	// }

	// @RequestMapping(value="/ontology/category/delete", method =
	// RequestMethod.DELETE)
	// public void deleteCategory(
	// @RequestParam String id,
	// HttpServletRequest request, HttpServletResponse response) {
	// ontoRepo.deleteCategory(id);
	// }

	// @RequestMapping(value="/ontology/list", method = RequestMethod.GET)
	// public @ResponseBody List<Ontology> getEntityList(@RequestParam String
	// id) {
	// return ontoRepo.getOntologyList(id);
	// }

	// @RequestMapping(value="/ontology/query", method = RequestMethod.GET)
	// public void getEntity(@RequestParam String id) {
	// return ontoRepo.getOntology(id);
	// }

	// @RequestMapping(value="/ontology/create", method= RequestMethod.GET)
	// public @ResponseBody String createOntology(
	// @RequestParam String title,
	// @RequestParam String description,
	// @RequestParam String category) {
	//
	// title = URLDecoder.decode(title, "utf-8");
	// description = URLDecoder.decode(description, "utf-8");
	// Ontology onto = new Ontology();
	// onto.setTitle(title);
	// onto.setDescription(description);
	// onto.setCategory(category);
	// String id = ontoRepo.createOntology(onto);
	// return id;
	// }

	// @RequestMapping(value="/ontology/update_title", method =
	// RequestMethod.GET)
	// public void updateOntologyTitle(
	// @RequestParam String id, @RequestParam String title) {
	// title = URLDecoder.decode(title, "utf-8");
	// ontoRepo.updateOntologyTitle(id, title);
	// }

	// @RequestMapping(value="/ontology/update_description", method =
	// RequestMethod.GET)
	// public void updateOntologyDescription(
	// @RequestParam String id, @RequestParam String description) {
	//
	// description = URLDecoder.decode(description, "utf-8");
	// ontoRepo.updateOntologyDescription(id, description);
	// }

	// @RequestMapping(value="/ontology/update_category", method =
	// RequestMethod.GET)
	// public void updateOntologyCategory(
	// @RequestParam String id, @RequestParam String category) {
	//
	// ontoRepo.updateOntologyCategory(id,category);
	// }
	//
	//
	// @RequestMapping(value="/ontology/delete", method = RequestMethod.GET)
	// public void deleteOntology(@RequestParam String id) {
	// ontoRepo.deleteOntology(id);
	// }

	// @RequestMapping(value="/ontology/create_member", method =
	// RequestMethod.GET)
	// public String createMember(
	// @RequestParam String id,
	// @RequestParam String title,
	// @RequestParam String type) {
	//
	// title = URLDecoder.decode(title, "utf-8");
	//
	// String ontoId = id;
	// Member member = new Member();
	// member.setTitle(title);
	// member.setType(type);
	//
	// String memberId = ontoRepo.createMember(ontoId, member);
	// return memberId;
	// }

	// @RequestMapping(value="/ontology/delete_member", method =
	// RequestMethod.GET)
	// public void deleteMember(
	// @RequestParam String id, @RequestParam String memberId) {
	//
	// String ontoId = id;
	// Ontology onto = ontoRepo.getOntology(ontoId);
	// List<Member> members = onto.getMembers();
	// boolean found = false;
	// for( Member member: members) {
	// String mid = member.getId();
	// if (mid.equals(memberId)) {
	// ontoRepo.deleteMember(ontoId, member);
	// json.put("retcode", 0);
	// json.put("msg", "success");
	// found = true;
	// break;
	// }
	// }
	//
	// if (!found) {
	// throw new Exception("member not exist");
	// }
	//
	// }

	// @RequestMapping(value="/ontology/update_member", method =
	// RequestMethod.GET)
	// public void updateMember(
	// @RequestParam String id,
	// @RequestParam String memberId,
	// @RequestParam String title, @RequestParam String type,
	// HttpServletRequest request, HttpServletResponse response) {
	//
	// title = URLDecoder.decode(title, "utf-8");
	// String ontoId = id;
	// Member member = new Member();
	// member.setId(memberId);
	// member.setTitle(title);
	// member.setType(type);
	//
	// ontoRepo.updateMember(ontoId, member);
	// }

	@RequestMapping(value = "/attribute/list", method = RequestMethod.GET)
	public @ResponseBody Attribute[] getAttributeList() {
		Recognition recog = new Recognition();
		return recog.getAttributes();
	}

	@RequestMapping(value = "/attribute/test", method = RequestMethod.GET)
	public @ResponseBody List<TestResult> testAttribute(
			@RequestParam String id, @RequestParam String samples) {

		List<TestResult> result = new ArrayList<TestResult>();

		Recognition recog = new Recognition();
		String[] items = samples.split("\n");
		for (String item : items) {
			TestResult test = new TestResult();
			test.sample = item;
			test.matched = id.equals(recog.match(item));
			result.add(test);
		}

		return result;
	}

	@RequestMapping(value = "/item/list", method = RequestMethod.GET)
	public @ResponseBody Pagination getItemList(@RequestParam String id,
			@RequestParam int pageNo, @RequestParam int pageSize) {
		return pointService.getItemList(id, pageNo, pageSize);
	}

	// @RequestMapping(value = "/item/analysis", method = RequestMethod.GET)
	// public void analysisItem(@RequestParam String seedId,
	// @RequestParam String itemId) {
	//
	// Item item = seedService.analysis(seedId, itemId);
	// }

	// @RequestMapping(value = "/point/network/generate", method =
	// RequestMethod.GET)
	// public void generateNetwork() {
	//
	//
	// pointService.generateNetwork();
	// NetworkStatus status = pointService.getNetworkStatus();
	// json.put("result", status.toJson());
	//
	// }

	@RequestMapping(value = "/point/status", method = RequestMethod.GET)
	public @ResponseBody NetworkStatus getNetworkStatus() {
		return pointService.getStatus();
	}

	//
	// // private final EntityService entityService;
	// //
	// // private final PointService pointService;
	//
	// // @Inject
	// // public WebController(SeedService crawlService) {
	// // this.crawlService = crawlService;
	// // }
	//
	// @RequestMapping(value="/", method = RequestMethod.GET)
	// public String printWelcome(ModelMap model) {
	// model.addAttribute("message", "Hello world!");
	// return "hello";
	// }
	//
	//

	// @RequestMapping(value="/ontology", method= RequestMethod.GET)
	// public void getOntologyCount(HttpServletRequest request,
	// HttpServletResponse response) {
	//
	// response.setContentType("text/javascript;charset=UTF-8");
	//
	// JSONObject json = new JSONObject();
	//
	// PrintWriter writer = null;
	// try {
	// writer = response.getWriter();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// String jsonpCallback = request.getParameter("callback");
	//
	// try {
	//
	// JSONArray result = pointService.getPointCount();
	//
	// json.put("result", result);
	// json.put("retcode", 0);
	// json.put("msg", "success");
	//
	// } catch(Exception e) {
	// try {
	// json.put("retcode", 1);
	// json.put("msg", e.toString());
	// } catch(Exception e2) {
	// e2.printStackTrace();
	// }
	// } finally {
	// String s = jsonpCallback + "(" + json.toString() + ");";
	// writer.print(s);
	// }
	// }

	// @RequestMapping(value="/entities", method= RequestMethod.GET)
	// public void getEntities(HttpServletRequest request, HttpServletResponse
	// response) {
	// response.setContentType("text/javascript;charset=UTF-8");
	//
	// JSONObject json = new JSONObject();
	//
	// PrintWriter writer = null;
	// try {
	// writer = response.getWriter();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// String jsonpCallback = request.getParameter("callback");
	//
	// try {
	//
	// JSONObject result = new JSONObject();
	// result.put("entity", entityService.getDictionary("entity"));
	// result.put("dict", entityService.getDictionary("dict"));
	//
	// json.put("result", result);
	// json.put("retcode", 0);
	// json.put("msg", "success");
	//
	// } catch(Exception e) {
	// try {
	// json.put("retcode", 1);
	// json.put("msg", e.toString());
	// } catch(Exception e2) {
	// e2.printStackTrace();
	// }
	// } finally {
	// String s = jsonpCallback + "(" + json.toString() + ");";
	// writer.print(s);
	// }
	// }

	//
	// @RequestMapping(value="/points", method = RequestMethod.GET)
	// public void getPointList(@RequestParam String name,
	// HttpServletRequest request,
	// HttpServletResponse response) {
	//
	// response.setContentType("text/javascript;charset=UTF-8");
	//
	// JSONObject json = new JSONObject();
	// PrintWriter writer = null;
	// try {
	// writer = response.getWriter();
	// } catch(Exception e) {
	// e.printStackTrace();
	// }
	//
	// String jsonpCallback = request.getParameter("callback");
	//
	// try {
	//
	// JSONArray result = pointService.getPointList(name);
	//
	// json.put("result", result);
	// json.put("retcode", 0);
	// json.put("msg", "success");
	//
	// } catch (Exception e) {
	// try {
	// json.put("retcode", 1);
	// json.put("msg", e.toString());
	// } catch (Exception e2) {
	// e2.printStackTrace();
	// }
	// } finally {
	// String s = jsonpCallback + "(" + json.toString() + ");";
	// writer.print(s);
	// }
	//
	// }
	//
	// @RequestMapping(value="/createpoint", method= RequestMethod.GET)
	// public void createPoint(HttpServletRequest request, HttpServletResponse
	// response) {
	//
	// response.setContentType("text/javascript;charset=UTF-8");
	//
	// JSONObject json = new JSONObject();
	// PrintWriter writer = null;
	// try {
	// writer = response.getWriter();
	// } catch(Exception e) {
	// e.printStackTrace();
	// }
	//
	// String jsonpCallback = request.getParameter("callback");
	//
	// try {
	// String param = request.getParameter("point").trim();
	// String jsonStr = URLDecoder.decode(param, "utf-8");
	// JSONObject point = new JSONObject(jsonStr);
	//
	// String id = entityService.createPoint(point);
	// json.put("result", id);
	// json.put("retcode", 0);
	// json.put("msg", "success");
	//
	// } catch (Exception e) {
	// try {
	// json.put("retcode", 1);
	// json.put("msg", e.toString());
	// } catch (Exception e2) {
	// e2.printStackTrace();
	// }
	// } finally {
	// String s = jsonpCallback + "(" + json.toString() + ");";
	// writer.print(s);
	// }
	//
	// }
	//
	// @RequestMapping(value="/updatepoint", method = RequestMethod.GET)
	// public void updatePoint(HttpServletRequest request, HttpServletResponse
	// response) {
	// response.setContentType("text/javascript;charset=UTF-8");
	//
	// JSONObject json = new JSONObject();
	// PrintWriter writer = null;
	// try {
	// writer = response.getWriter();
	// } catch(Exception e) {
	// e.printStackTrace();
	// }
	//
	// String jsonpCallback = request.getParameter("callback");
	//
	// try {
	// String param = request.getParameter("point").trim();
	// String jsonStr = URLDecoder.decode(param, "utf-8");
	// JSONObject point = new JSONObject(jsonStr);
	//
	// entityService.updatePoint(point);
	// json.put("retcode", 0);
	// json.put("msg", "success");
	//
	// } catch (Exception e) {
	// try {
	// json.put("retcode", 1);
	// json.put("msg", e.toString());
	// } catch (Exception e2) {
	// e2.printStackTrace();
	// }
	// } finally {
	// String s = jsonpCallback + "(" + json.toString() + ");";
	// writer.print(s);
	// }
	// }
	//
	// @RequestMapping(value="/deletepoint", method = RequestMethod.GET)
	// public void deletePoint(@RequestParam String id, @RequestParam String
	// ontology,
	// HttpServletRequest request, HttpServletResponse response) {
	//
	// response.setContentType("text/javascript;charset=UTF-8");
	//
	// JSONObject json = new JSONObject();
	// PrintWriter writer = null;
	// try {
	// writer = response.getWriter();
	// } catch(Exception e) {
	// e.printStackTrace();
	// }
	//
	// String jsonpCallback = request.getParameter("callback");
	//
	// try {
	// entityService.deletePoint(id, ontology);
	// json.put("retcode", 0);
	// json.put("msg", "success");
	// } catch (Exception e) {
	// try {
	// json.put("retcode", 1);
	// json.put("msg", e.toString());
	// } catch (Exception e2) {
	// e2.printStackTrace();
	// }
	// } finally {
	// String s = jsonpCallback + "(" + json.toString() + ");";
	// writer.print(s);
	// }
	// }
	//
	//
	//
	// @RequestMapping(value="/block", method=RequestMethod.GET)
	// public void getBlock(@RequestParam String title,
	// HttpServletRequest request, HttpServletResponse response) {
	// response.setContentType("text/javascript;charset=UTF-8");
	//
	// JSONObject json = new JSONObject();
	//
	// PrintWriter writer = null;
	// try {
	// writer = response.getWriter();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// String jsonpCallback = request.getParameter("callback");
	//
	// try {
	// JSONObject block = pointService.getBlock(title);
	// json.put("result", block);
	// json.put("retcode", 0);
	// json.put("msg", "success");
	//
	// } catch(Exception e) {
	// try {
	// json.put("retcode", 1);
	// json.put("msg", e.toString());
	// } catch(Exception e2) {
	// e2.printStackTrace();
	// }
	// } finally {
	// String s = jsonpCallback + "(" + json.toString() + ");";
	// writer.print(s);
	// }
	//
	// }
	//
	//
	// @RequestMapping(value="/relation", method= RequestMethod.GET)
	// public void getRelation(@RequestParam String title, @RequestParam Integer
	// degree,
	// HttpServletRequest request, HttpServletResponse response) {
	//
	// response.setContentType("text/javascript;charset=UTF-8");
	//
	// JSONObject json = new JSONObject();
	//
	// PrintWriter writer = null;
	// try {
	// writer = response.getWriter();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// String jsonpCallback = request.getParameter("callback");
	//
	//
	// try {
	// JSONArray blocks = new JSONArray();
	// JSONArray links = new JSONArray();
	//
	// generateLinkPoint(title, blocks, links, degree);
	//
	// JSONObject result = new JSONObject();
	// result.put("blocks", blocks);
	// result.put("links", links);
	//
	// json.put("result", result);
	// json.put("retcode", 0);
	// json.put("msg", "success");
	//
	// } catch(Exception e) {
	// try {
	// json.put("retcode", 1);
	// json.put("msg", e.toString());
	// } catch(Exception e2) {
	// e2.printStackTrace();
	// }
	// } finally {
	// String s = jsonpCallback + "(" + json.toString() + ");";
	// writer.print(s);
	// }
	// }
	//
	// private void generateLinkPoint(String title, JSONArray blocks, JSONArray
	// links, Integer degree)
	// throws Exception{
	//
	// blocks.put(title);
	//
	// if (degree <= 0 ) {
	// return;
	// }
	//
	// List<String> titles = pointService.getLinks(title);
	// for(String to : titles) {
	//
	// JSONObject link = new JSONObject();
	// link.put("from", title);
	// link.put("to", to);
	// links.put(link);
	//
	// generateLinkPoint(to, blocks, links, degree-1);
	// }
	// }

	// @RequestMapping(value="/dictionary", method=RequestMethod.GET)
	// public void getDictionary(@RequestParam String name, HttpServletRequest
	// request, HttpServletResponse response) {
	// response.setContentType("text/javascript;charset=UTF-8");
	//
	// JSONObject json = new JSONObject();
	//
	// PrintWriter writer = null;
	// try {
	// writer = response.getWriter();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// String jsonpCallback = request.getParameter("callback");
	//
	// try {
	//
	// json.put("result", entityService.getDictionary(name));
	// json.put("retcode", 0);
	// json.put("msg", "success");
	//
	// } catch(Exception e) {
	// try {
	// json.put("retcode", 1);
	// json.put("msg", e.toString());
	// } catch(Exception e2) {
	// e2.printStackTrace();
	// }
	// } finally {
	// String s = jsonpCallback + "(" + json.toString() + ");";
	// writer.print(s);
	// }
	// }
}
