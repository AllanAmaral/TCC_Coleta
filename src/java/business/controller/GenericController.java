package business.controller;

import business.objects.Lixeira;
import java.math.BigDecimal;
import java.util.Date;

public class GenericController {

    public GenericController() {
    }

    public String getImagemStatus(Lixeira lixeira) {
        String img = null;
        switch (getStatus(lixeira)) {
            case 1:
                img = "lixeira-vermelha-icon.png";
                break;
            case 2:
                img = "lixeira-amarela-icon.png";
                break;
            case 3:
                img = "lixeira-azul-icon.png";
                break;
        }
        return img;
    }

    /*
    *   1 - Alto    - Vermelho
    *   2 - Médio   - Amarelo
    *   3 - Baixo   - Azul
     */
    public int getStatus(Lixeira lixeira) {
        int cor = 3;

        if (lixeira == null) {
            return cor;
        }

        if (lixeira.getColetadoLixeiraKg()
                .compareTo(lixeira.getCapacidadeLixeiraKg()
                        .multiply(new BigDecimal(0.3))) < 1) {
            cor = 3;
        }
        if ((lixeira.getColetadoLixeiraKg()
                .compareTo(lixeira.getCapacidadeLixeiraKg()
                        .multiply(new BigDecimal(0.7))) < 1)
                && (lixeira.getColetadoLixeiraKg()
                .compareTo(lixeira.getCapacidadeLixeiraKg()
                        .multiply(new BigDecimal(0.3))) > -1)) {
            cor = 2;
        }
        if (lixeira.getColetadoLixeiraKg()
                .compareTo(lixeira.getCapacidadeLixeiraKg()
                        .multiply(new BigDecimal(0.7))) > -1) {
            cor = 1;
        }
        return cor;
    }

    public void validaDataLimite(Date dataInicial, Date dataFinal) throws Exception {
        if (dataInicial == null && dataFinal == null) {
            return;
        } else if (dataInicial == null || dataFinal == null || dataFinal.before(dataInicial)) {
            throw new Exception("A data final deve ser maior ou igual a data inicial.");
        }
    }
}
