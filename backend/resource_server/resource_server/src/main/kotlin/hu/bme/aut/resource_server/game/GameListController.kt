//package hu.bme.aut.resource_server.game
//
//import org.springframework.http.HttpStatus
//import org.springframework.http.ResponseEntity
//import org.springframework.web.bind.annotation.GetMapping
//import org.springframework.web.bind.annotation.RequestMapping
//import org.springframework.web.bind.annotation.RestController
//
//
///**
// * TODO!!!!
// */
//@RestController
//@RequestMapping("/games")
//class GameListController {
//    @GetMapping("/all")
//    fun getAllGames(): ResponseEntity<List<GameDao>> {
//        val games = //TODO
//        return ResponseEntity(games, HttpStatus.OK)
//    }
//    @GetMapping("/all/filtered")
//    fun getUserInfo(filter: List<String>): ResponseEntity<List<GameDao>> {
//        val games = //TODO
//        return ResponseEntity(games, HttpStatus.OK)
//    }
//    @GetMapping("/{user_id}/teachers")
//    fun getRecommenderTeachers(@PathVariable ( "user_id" )): ResponseEntity<List<UserDao>> {
//        val teachers = // TODO
//        return ResponseEntity(teachers, HttpStatus.OK)
//    }
//    @GetMapping("/{user_id}/teachers/{teacher_id}")
//    fun getRecommenderTeachers(@PathVariable ( "user_id" ), @PathVariable ( "teacher_id" )): ResponseEntity<List<GameDao>> {
//        val games = // TODO
//                return ResponseEntity(games, HttpStatus.OK)
//    }
//    @GetMapping("/{user_id}/researchers")
//    fun getRecommenderTeachers(@PathVariable ( "user_id" )): ResponseEntity<List<UserDao>> {
//        val teachers = // TODO
//                return ResponseEntity(teachers, HttpStatus.OK)
//    }
//    @GetMapping("/{user_id}/teachers/{researcher_id}")
//    fun getRecommenderTeachers(@PathVariable ( "user_id" ), @PathVariable ( "researcher_id" )): ResponseEntity<List<GameDao>> {
//        val games = // TODO
//        return ResponseEntity(games, HttpStatus.OK)
//    }
//}