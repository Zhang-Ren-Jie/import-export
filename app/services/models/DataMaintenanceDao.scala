package services.models

import com.google.inject.ImplementedBy

/**
  * Created by zrj on 17-6-8.
  */
@ImplementedBy(classOf[DataMaintenanceDaoImp])
trait DataMaintenanceDao {



}

class DataMaintenanceDaoImp extends DataMaintenanceDao {

}