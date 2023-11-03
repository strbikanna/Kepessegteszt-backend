package hu.bme.aut.resource_server.game

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
class LifeController {
    @RequestMapping("important_question")
    fun askYouOut() {
        println("Lenne kedved közösen eltölteni egy délutánt egy (vagy több) finom forrócsoki mellett?")
    }
}